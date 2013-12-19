package com.ever365.ecm.service.listener;

import java.io.InputStream;

import com.ever365.ecm.entity.Entity;

/**
 * Registered events for repository.
 * @author Liu Han
 */

public interface RepositoryListener {
	
	public void beforeFileUpload(String repository, String path, String name,
			InputStream is, long size);
	
	public void onFileUploaded(Entity entity);
	
	public void onFolderCreated(Entity entity);
	
	public void onMoved(Entity srcEntity, Entity targetEntity);
	
	public void onCopied(Entity srcEntity, Entity targetEntity);
	
	public void onDeleted(Entity entity);

	public void onRecovered(Entity entity);
	
	public String getName();
	
	public void setEnabled(boolean enable);
	
	public boolean enabled();
}
