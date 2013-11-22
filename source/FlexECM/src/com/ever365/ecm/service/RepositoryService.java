package com.ever365.ecm.service;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ever365.ecm.authority.AuthenticationUtil;
import com.ever365.ecm.clipboard.ClipboardDAO;
import com.ever365.ecm.content.ContentDAO;
import com.ever365.ecm.content.ContentStore;
import com.ever365.ecm.content.ContentStoreDAO;
import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.entity.EntityDAO;
import com.ever365.ecm.permission.ACE;
import com.ever365.ecm.permission.PermissionService;
import com.ever365.ecm.repo.Model;
import com.ever365.ecm.repo.QName;
import com.ever365.ecm.repo.Repository;
import com.ever365.ecm.repo.RepositoryDAO;
import com.ever365.ecm.service.listener.RepositoryListener;
import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;
import com.ever365.utils.UUID;

/**
 * Repository and Content basic operations
 * @author han
 */

public class RepositoryService {

	private static Logger logger = Logger.getLogger(RepositoryService.class.getName());
	
	private EntityDAO entityDAO;
	private ContentDAO contentDAO;
	private RepositoryDAO repositoryDAO;
	private PermissionService permissionService;
	private ClipboardDAO clipboardDAO;
	
	public void setClipboardDAO(ClipboardDAO clipboardDAO) {
		this.clipboardDAO = clipboardDAO;
	}
	public void setRepositoryDAO(RepositoryDAO repositoryDAO) {
		this.repositoryDAO = repositoryDAO;
	}
	
	private ContentStoreDAO contentStoreDAO;
	
	private List<RepositoryListener> listeners; 
	
	public void setContentStoreDAO(ContentStoreDAO contentStoreDAO) {
		this.contentStoreDAO = contentStoreDAO;
	}
	public void setContentDAO(ContentDAO contentDAO) {
		this.contentDAO = contentDAO;
	}
	public void setListeners(List<RepositoryListener> listeners) {
		this.listeners = listeners;
	}
	
	public PermissionService getPermissionService() {
		return permissionService;
	}
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}
	public EntityDAO getEntityDAO() {
		return entityDAO;
	}

	public void removeAspect(String repo, String aspect) {
	}

	public void removeRepository(String repository) {
		// TODO Auto-generated method stub
	}
	
	@RestService(uri="/repository/list/all", method="GET", runAsAdmin=true)
	public List<Map<String, Object>> getAllRepositories() {
		List<Repository> allrepo = repositoryDAO.getRepositories();
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		for (Repository repository : allrepo) {
			Map<String, Object> m = getRepositoryInfo(repository);
			result.add(m);
		}
		return result;
	}
	
	
	@RestService(uri="/repository/public/list", method="GET")
	public List<Map<String, Object>> getRepositories() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		/*
		List<Repository> ownedRepositories = repositoryDAO.getRepositoriesByOwner(AuthenticationUtil.getCurrentUser());
		for (Repository repository : ownedRepositories) {
			Map<String, Object> m = getRepositoryInfo(repository);
			result.add(m);
		}
		*/
		List<Repository> list = repositoryDAO.getRepositories("pub");
		
		for (Repository repository : list) {
			Map<String, Object> m = getRepositoryInfo(repository);
			result.add(m);
		}
		return result;
	}
	
	public Map<String, Object> getRepositoryInfo(Repository repository) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("id", repository.toString());
		m.put("name", repository.getIdentifier());
		m.put("owner", repository.getOwner());
		m.put("root", getEntityInfo(repository.getRootEntity(), 5));
		m.put("trash", repository.getTrashEntity().getId());
		return m;
	}
	
	@RestService(uri="/repository/drop", method="POST")
	public void dropRepository(@RestParam(value="repository")String repository) {
		
	}
	
	@RestService(uri="/repository/entity/childcontainer", method="GET")
	public Map<String, Object> getChildContainer(@RestParam(value="pid")String id, @RestParam(value="skip")int skip, @RestParam(value="limit")int limit) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (id==null) {
			Repository userRepo = getUserRepo();
			id = userRepo.getRootEntity().getId();
		}
		
		Entity current = entityDAO.getEntityById(id);
		//List<ACE> permissions = permissionService.getPermissions(current, true);
		
		result.putAll(current.toMap());
		
		List<Entity> children = entityDAO.listChildByType(id, Model.TYPE_FOLDER.toString(), skip, limit);
		
		List<Map<String, Object>> converted = new ArrayList<Map<String,Object>>();
		for (Entity entity : children) {
			Map<String, Object> rm = entity.toMap();
			if (entity.getAcl()) {
				rm.put("permission", permissionService.getPermissions(entity, AuthenticationUtil.getCurrentUser()));
			}
			converted.add(rm);
		}
		
		//result.put("permission", permissions);
		result.put("list", converted);
		return result;
	}
	
	@RestService(uri="/repository/entity/list", method="GET")
	public Map<String, Object> getChildren(@RestParam(value="repository")String repository,
			@RestParam(value="id")String id, @RestParam(value="skip")int skip, @RestParam(value="limit")int limit) {
		if (id==null) {
			Repository userRepo = getUserRepo();
			if (userRepo==null) throw new HttpStatusException(HttpStatus.SERVICE_UNAVAILABLE);
			id = userRepo.getRootEntity().getId();
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		Entity current = entityDAO.getEntityById(id);
		result.putAll(current.toMap());
		
		List<Entity> children = entityDAO.listChild(current, skip, limit);
		
		List<Map<String, Object>> converted = new ArrayList<Map<String,Object>>();
		for (Entity entity : children) {
			Map<String, Object> rm = entity.toMap();
			rm.put("acl", entity.getAcl());
			converted.add(rm);
		}
		
		result.put("role", permissionService.getPermissions(current, AuthenticationUtil.getCurrentUser()));
		
		result.put("list", converted);
		return result;
	}
	public Repository getUserRepo() {
		return repositoryDAO.getRepository("usr://" + AuthenticationUtil.getCurrentUser(), false);
	}
	
	public String getParentId(String path, Repository repo) {
		String parentEntityId = null;
		
		if (path!=null) {
			if (path.equals("/") || path.equals("") || path.equals("null")) return repo.getRootEntity().getId();
			
			if (path.startsWith("/")) {
				Entity parentEntity = entityDAO.getEntityByPath(repo.getRootEntity(), path);
				if (parentEntity==null) {
					throw new HttpStatusException(HttpStatus.NOT_FOUND);
				}
				parentEntityId = parentEntity.getId();
			} else {
				/* since we are using mongodb, the extra check of parent is ignored.*/
				parentEntityId = path;
			}
		}
		return parentEntityId;
	}

	@RestService(uri="/file/upload", method="POST", multipart=true)
	public Map<String, Object> addFile(@RestParam(value="id") String parentEntityId, @RestParam(value="name")String name,
			@RestParam(value="file")InputStream is, @RestParam(value="size")Long size
			) {
		
		if (parentEntityId==null) {
			throw new HttpStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
		
		Entity parentEntity = entityDAO.getEntityById(parentEntityId);
		
		if (parentEntity==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		
		Repository repo = repositoryDAO.getRepository(parentEntity.getRepository().toString(), false);
		
		String childNodeName = name;
		
		Map<QName, Serializable> specialProperties = new HashMap<QName, Serializable>();
		
		ContentStore contentStore = contentStoreDAO.getContentStore(repo.getStoreName());
		
		String url = contentStore.putContent(is, size);
		
		String contentDataId = contentDAO.createContentData(url, null, size, null);
		
		specialProperties.put(Model.PROP_FILE_URL, contentDataId);
		specialProperties.put(Model.PROP_FILE_SIZE, size);
		
		if (childNodeName.lastIndexOf(".")>-1) {
			specialProperties.put(Model.PROP_FILE_EXT, childNodeName.substring(childNodeName.lastIndexOf(".")+1));
		}
		
		Entity entity = entityDAO.addEntity(repo, parentEntityId, Model.FS_CONTAINS, 
				null, Model.TYPE_FILE, childNodeName, null, specialProperties);
		
		for (RepositoryListener listener : listeners) {
			if (listener.enabled()) {
				listener.onFileUploaded(entity);
			}
		}
		return entity.toMap();
	}
	public Repository getRepository(String repository) {
		Repository repo = repositoryDAO.getRepository(repository, false);
		if (repo==null) {
			throw new HttpStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
		return repo;
	}
	
	@RestService(uri="/folder/create", method="POST")
	public Map<String, Object> addFolder(
			@RestParam(value="id") String parentEntityId, @RestParam(value="name")String name,
			@RestParam(value="desc")String desc, @RestParam(value="title")String title
			) {
		if (parentEntityId==null) {
			throw new HttpStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
		Entity parentEntity = entityDAO.getEntityById(parentEntityId);
		
		if (parentEntity==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		
		String uuid = UUID.generate();
		String childNodeName = name;
		
		Map<QName, Serializable> specialProperties = new HashMap<QName, Serializable>();
		specialProperties.put(Model.PROP_FOLDER_TOTAL, 0L);
		specialProperties.put(Model.PROP_FILE_TOTAL, 0L);
		specialProperties.put(Model.PROP_FILE_SIZE, 0L);
		if (desc!=null) {
			specialProperties.put(Model.PROP_DESC, desc);
			specialProperties.put(Model.PROP_TITLE, title);
		}
		
		Entity entity = entityDAO.addEntity(parentEntity.getRepository(), parentEntityId, Model.FS_CONTAINS, 
				uuid, Model.TYPE_FOLDER, childNodeName, null, specialProperties);
		
		for (RepositoryListener listener : listeners) {
			if (listener.enabled()) {
				listener.onFolderCreated(entity);
			}
		}
		return entity.toMap();
	}
	
	
	
	@RestService(uri="/file/moveToTrash", method="POST")
	public void moveToTrash(
			@RestParam(value="files", required=true) List<String> files) {
		
		
		for (String id : files) {
			Entity entity = entityDAO.getEntityById(id);
			if (entity==null) continue;
			
			Repository repo = repositoryDAO.getRepository(entity.getRepository().toString(), false);
			
			Map<QName, Serializable> map = new HashMap<QName, Serializable>();
			
			String randomName = UUID.generate();
			map.put(Model.PROP_ORIGIN_NAME, entity.getName());
			map.put(Model.PROP_ORIGIN_PATH, entity.getParentId());
			map.put(Model.PROP_NAME, randomName);
			map.put(Model.PROP_MODIFIED, System.currentTimeMillis());
			map.put(Model.PROP_MODIFIER, AuthenticationUtil.getCurrentUser());
			entityDAO.move(entity, repo.getTrashEntity(), map);
		}
		
	}
	
	@RestService(uri="/entity/trash/list", method="GET")
	public List<Map<String, Object>> getTrashedEntity(@RestParam(value="repo") String repo) {
		if (repo==null) {
			throw new HttpStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
		
		Repository repository = repositoryDAO.getRepository(repo, false);
		
		List<Entity> children = entityDAO.listChild(repository.getTrashEntity(), 0, 10000);
		
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		for (Entity entity : children) {
			Map<String, Object> rm = entity.toMap();
			
			result.add(rm);
		}
		return result;
	}
	
	@RestService(uri="/entity/remove", method="POST")
	public void removeEntity(
			@RestParam(value="id") String ids) {
		if (ids==null) {
			throw new HttpStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
		String[] idlist = ids.split(",");
		for (String id : idlist) {
			Entity entity = entityDAO.getEntityById(id);
			
			if (entity==null) {
				continue;
			}
			entityDAO.deleteEntity(entity);
		}
	}
	
	@RestService(uri="/file/move", method="POST")
	public void move(
			@RestParam(value="srcPath",required=true) List<String> srcPaths,
			@RestParam(value="targetPath")String targetPath) {
		Entity target = getNotNullEntity(targetPath);
		
		for (String path : srcPaths) {
			Entity src = entityDAO.getEntityById(path);
			if (src == null) continue;
			entityDAO.move(src, target, null);
			for (RepositoryListener listener : this.listeners) {
				if (listener.enabled()) {
					listener.onMoved(src, target);
				}
			}
		}
	}
	
	/**
	 * Get the entity and make sure it is not null(If null, exception will be thrown)
	 * @param targetPath
	 * @return
	 */
	public Entity getNotNullEntity(String targetPath) {
		Entity target = null;
		
		if (targetPath==null) {
			Repository userRepo = getUserRepo();
			if (userRepo==null) throw new HttpStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		} else {
			target = entityDAO.getEntityById(targetPath);
		}
		
		if (target==null) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		}
		return target;
	}
	
	@RestService(uri="/file/rename", method="POST")
	public void rename(
			@RestParam(value="src",required=true) String src,
			@RestParam(value="newName",required=true) String newName) {
		Entity srcEntity = entityDAO.getEntityById(src);
		
		if (srcEntity==null) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		}
		
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		props.put(Model.PROP_NAME, newName);
		
		entityDAO.move(srcEntity, null, props);
	}
	


	@RestService(uri="/file/recover", method="POST")
	public void recover (
			@RestParam(value="ids", required=true) List<String> idlist) {
		if (idlist==null) {
			throw new HttpStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
		
		for (String string : idlist) {
			Entity entity = entityDAO.getEntityById(string);
			if (entity==null) continue;
			recoverItem(entity);
		}
	}
	
	
	public void recoverItem(Entity entity) {
		if (entity==null) return;
		Map<QName, Serializable> map = new HashMap<QName, Serializable>();
		
		Entity originalParent = entityDAO.getEntityById(entity.getPropertyStr(Model.PROP_ORIGIN_PATH));
		
		if (originalParent==null) throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		
		map.put(Model.PROP_ORIGIN_NAME, null);
		map.put(Model.PROP_ORIGIN_PATH, null);
		map.put(Model.PROP_RECOVERED, true);
		map.put(Model.PROP_NAME, entity.getPropertyStr(Model.PROP_ORIGIN_NAME));

		map.put(Model.PROP_MODIFIED, System.currentTimeMillis());
		map.put(Model.PROP_MODIFIER, AuthenticationUtil.getCurrentUser());
		
		entityDAO.move(entity, originalParent, map);
		
		for (RepositoryListener listener : this.listeners) {
			if (listener.enabled()) {
				listener.onRecovered(entity);
			}
		}
	}

	@RestService(uri="/file/recoverAll", method="POST")
	public void recoverAll(@RestParam(value="repo",required=true)String sr) {
		
		Repository repository = repositoryDAO.getRepository(sr, false);
		
		if (repository!=null) {
			Entity trashEntity = repository.getTrashEntity();
			
			List<Entity> list = entityDAO.listChild(trashEntity, 0, -1);
			
			for (Entity entity : list) {
				try {
					recoverItem(entity);
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	@RestService(uri="/trash/clean", method="POST")
	public void cleanTrash(@RestParam(value="repo",required=true)String sr) {
		
		Repository repository = repositoryDAO.getRepository(sr, false);
		
		if (repository!=null) {
			Entity trashEntity = repository.getTrashEntity();
			
			List<Entity> list = entityDAO.listChild(trashEntity, 0, -1);
			
			for (Entity entity : list) {
				try {
					entityDAO.deleteEntity(entity);
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	@RestService(uri="/share/update", method="POST")
	public void updatePermission(@RestParam(value="id", required=true)String id,@RestParam(value="inherit")Boolean inherit,
			@RestParam(value="aces",required=true) List<Map<String, Object>> aces) {
		
		Entity entity = entityDAO.getEntityById(id);
		if (entity==null) {
			throw new HttpStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
		
		permissionService.clearPermission(entity);
		
		permissionService.setInheritParentPermissions(entity, inherit);
		
		Map<QName, Serializable> specialProperties = new HashMap<QName, Serializable>();
		specialProperties.put(Model.PROP_ACL, false);
		if (aces!=null) {
			for (Map<String, Object> m : aces) {
				permissionService.setPermission(entity, (String)m.get("auth"), (String)m.get("perm"), true);
			}
			if (aces.size()>0) {
				specialProperties.put(Model.PROP_ACL, true);
			}
		} 
		entityDAO.updateEntityProperties(entity, specialProperties);
		return;
	}
	
	@RestService(uri="/share/mine", method="GET")
	public List<Map<String, Object>> getMyShares() {
		
		Collection<Entity> sources = permissionService.getEntitiesBySource(AuthenticationUtil.getCurrentUser());
		
		List<Map<String, Object>> shares = new ArrayList<Map<String,Object>>();
		
		for (Entity entity : sources) {
			Map<String, Object> info = getEntityInfo(entity, 5);
			shares.add(info);
		}
		return shares;
	}
	
	public Map<String, Object> getEntityInfo(Entity entity, int limit) {
		Map<String, Object> info = entity.toMap();
		List<Entity> childrens = entityDAO.listChild(entity, 0, limit);
		
		List<Map<String, Object>> details = new ArrayList<Map<String,Object>>();
		for (Entity child : childrens) {
			details.add(child.toMap());
		}
		
		info.put("list", details);
		return info;
	}
	
	@RestService(uri="/share/received", method="GET")
	public List<Map<String, Object>> getSharesToMe() {
		List<Map<String, Object>> shares = new ArrayList<Map<String,Object>>();
		
		Collection<Entity> sources = permissionService.getEntitiesByTarget(AuthenticationUtil.getCurrentUser());
		
		for (Entity entity : sources) {
			Map<String, Object> info = getEntityInfo(entity, 5);
			shares.add(info);
		}
		return shares;
	}
	
	@RestService(uri="/share/list", method="GET")
	public Map<String, Object> getPermissions(@RestParam(value="id", required=true)String id) {
		
		Map<String, Object> pinfo = new HashMap<String, Object>();
		
		Entity entity = entityDAO.getEntityById(id);
		
		List<ACE> permissions = permissionService.getAllSetPermissions(entity, true);
		
		pinfo.put("inh", permissionService.getInheritParentPermissions(entity));

		List<Map<String, Object>> inhlist = new ArrayList<Map<String,Object>>();
		
		List<Map<String, Object>> curlist = new ArrayList<Map<String,Object>>();
		
		for (ACE ace : permissions) {
			
			Map<String, Object> info = new HashMap<String, Object>();
			info.put("auth", ace.getAuthority());
			info.put("permission", ace.getPermission());
			info.put("allow", ace.isAllow());
			info.put("entity", ace.getEntity());
			
			if (ace.getEntity().equals(entity.getId())) {
				curlist.add(info);
			} else {
				inhlist.add(info);
			}
		}

		pinfo.put("curlist", curlist);
		pinfo.put("inhlist", inhlist);
		return pinfo;
	}
	
	
	@RestService(uri="/permission/list/mine", method="GET")
	public void getMyPermissions(@RestParam(value="id")String id) {
		
	}
	
	@RestService(uri="/file/clipboard/copyAll", method="POST")
	public void copyFromClipboard(@RestParam(value="target", required=true)String target) {
		List<Map<String, Object>> clips = clipboardDAO.list();
		
		for (Map<String, Object> map : clips) {
			
			try {
				
			} catch (HttpStatusException e) {
				//do nothing
			}
		}
	}
	
	@RestService(uri="/file/copy", method="POST")
	public void copy(@RestParam(value="srcs",required=true) List<String> srcPaths, @RestParam(value="target")String targetId
			) {
		Entity target = getNotNullEntity(targetId);
		for (String path : srcPaths) {
			Entity src = entityDAO.getEntityById(path);
			if (src == null) continue; 
			try {
				entityDAO.copy(src, target, null);
				for (RepositoryListener listener : this.listeners) {
					if (listener.enabled()) {
						listener.onCopied(src, target);
					}
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	
	
}
