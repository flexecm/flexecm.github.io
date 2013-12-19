package com.ever365.ecm.service.listener;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.entity.EntityDAO;
import com.ever365.ecm.repo.Model;
import com.ever365.ecm.repo.QName;

/**
 * Collect and control file and folder informations(size and count)
 * @author Liu Han
 */

public class EntityStaticsCollector implements RepositoryListener {
	
	private EntityDAO entityDAO;
	private Boolean enabled;
	
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Override
	public void beforeFileUpload(String repository, String path, String name,
			InputStream is, long size) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFileUploaded(Entity entity) {
		LinkedList<Entity> ancestors = entityDAO.getAncestor(entity);
		if (ancestors==null) return;
		
		for (Entity ancestor : ancestors) {
			Map<QName, Serializable> specialProperties = new HashMap<QName, Serializable>();
			specialProperties.put(Model.PROP_FILE_SIZE, "+" + entity.getSize());
			specialProperties.put(Model.PROP_FILE_TOTAL, "+1");
			entityDAO.updateEntityProperties(ancestor, specialProperties);
		}
	}

	@Override
	public String getName() {
		return "EntityStaticsCollector";
	}

	@Override
	public void setEnabled(boolean e) {
		this.enabled = e;
	}

	@Override
	public boolean enabled() {
		return enabled;
	}

	@Override
	public void onFolderCreated(Entity entity) {
		LinkedList<Entity> ancestors = entityDAO.getAncestor(entity);
		if (ancestors==null) return;
		for (Entity ancestor : ancestors) {
			Map<QName, Serializable> specialProperties = new HashMap<QName, Serializable>();
			specialProperties.put(Model.PROP_FOLDER_TOTAL, "+1");
			entityDAO.updateEntityProperties(ancestor, specialProperties);
		}
	}

	@Override
	public void onMoved(Entity srcEntity, Entity targetEntity) {
		
		LinkedList<Entity> decrease = entityDAO.getAncestor(srcEntity);
		
		LinkedList<Entity> increase = entityDAO.getAncestor(targetEntity);
		increase.add(targetEntity);
		
		Long fileTotal = 0L;
		Long folderTotal = 0L;
		Long size = 0L;
		if (srcEntity.getType().equals(Model.TYPE_FILE)) {
			fileTotal = 1L;
		} else {
			fileTotal = (Long)srcEntity.getProperty(Model.PROP_FILE_TOTAL);
			folderTotal = (Long)srcEntity.getProperty(Model.PROP_FOLDER_TOTAL) + 1;
		}
		size = (Long)srcEntity.getProperty(Model.PROP_FILE_SIZE);
		
		for (Entity src : decrease) {
			if (increase.contains(src)) {
				continue;
			}
			Map<QName, Serializable> specialProperties = new HashMap<QName, Serializable>();
			specialProperties.put(Model.PROP_FILE_SIZE, "+" + (-size));
			specialProperties.put(Model.PROP_FILE_TOTAL, "+" + (-fileTotal));
			specialProperties.put(Model.PROP_FOLDER_TOTAL, "+" + (-folderTotal));
			entityDAO.updateEntityProperties(src, specialProperties);
		}
		
		for (Entity target : increase) {
			if (decrease.contains(target)) {
				continue;
			}
			Map<QName, Serializable> specialProperties = new HashMap<QName, Serializable>();
			specialProperties.put(Model.PROP_FILE_SIZE, "+" + size);
			specialProperties.put(Model.PROP_FILE_TOTAL, "+" + fileTotal);
			specialProperties.put(Model.PROP_FOLDER_TOTAL, "+" + folderTotal);
			entityDAO.updateEntityProperties(target, specialProperties);
		}
	}

	@Override
	public void onCopied(Entity srcEntity, Entity targetEntity) {
		
	}

	@Override
	public void onDeleted(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecovered(Entity entity) {
		// TODO Auto-generated method stub
		
	}

}
