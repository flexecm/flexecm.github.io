package com.ever365.ecm.authority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.entity.EntityDAO;
import com.ever365.ecm.repo.Model;
import com.ever365.ecm.repo.QName;
import com.ever365.ecm.repo.Repository;
import com.ever365.ecm.repo.RepositoryDAO;
import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;
import com.ever365.utils.UUID;

/**
 * Simple implements of person service which use a flat store 
 * @author LiuHan
 */

public class PersonService {

	private EntityDAO entityDAO;
	private RepositoryDAO repositoryDAO;
	
	private String repoName;
	private String adminPassword;
	
	private String allUser;
	
	public void setAllUser(String allUser) {
		this.allUser = allUser;
	}

	public void setRepositoryDAO(RepositoryDAO repositoryDAO) {
		this.repositoryDAO = repositoryDAO;
	}
	
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public void setPersonRepo(String personRepo) {
		repoName = personRepo;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public Entity getPerson(String userName) {
		Entity personEntity = getPeopleEntity(userName);
		return personEntity;
	}

	public Repository getPersonRepository() {
		if (repoName==null) {
			repoName = "person://all";
		}
		return repositoryDAO.getRepository(repoName, true);
	}

	@RestService(method="GET", uri="/person/exist")
	public boolean personExists(@RestParam(value="name")String userName) {
		return (getPerson(userName)!=null);
	}
	
	@RestService(method="GET", uri="/person/search")
	public List<Map<String, Object>> searchPerson(@RestParam(value="name")String name) {
		
		List<Entity> entities = entityDAO.listChild(getPersonRepository().getRootEntity(), 0, -1);
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>(entities.size());
		
		for (Entity entity : entities) {
			
			result.add(entity.toMap());
		}
		return result;
	}
	
	@RestService(method="GET", uri="/user/filter")
	public List<Map<String, Object>> filterPerson(@RestParam(value="user")String name) {
		
		List<Entity> entities = entityDAO.listChild(getPersonRepository().getRootEntity(), 0, -1);
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>(entities.size());
		
		for (Entity entity : entities) {
			
			result.add(entity.toMap());
		}
		return result;
	}
	
	public void init() {
		
	}
	
	@RestService(method="GET", uri="/person/list")
	public List<Map<String, Object>> listPersons(@RestParam(value="skip")int skip, @RestParam(value="limit")int limit ) {
		List<Entity> entities = entityDAO.listChild(getPersonRepository().getRootEntity(), skip, limit);
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>(entities.size());
		
		for (Entity entity : entities) {
			result.add(entity.toMap());
		}
		return result;
	}
	
	@RestService(method="POST", uri="/person/create")
	public Entity createPerson(@RestParam(value="name")String userName,
			@RestParam(value="password")String password,
			@RestParam(value="quota")long quota,
			@RestParam(value="admin")Boolean admin,
			@RestParam(value="email")String email,
			@RestParam(value="dept")String dept, @RestParam(value="store")String conentStoreName
	) {
		Map<QName, Serializable> maps = new HashMap<QName, Serializable>();
		
		maps.put(Model.PROP_PASSWORD, password);
		maps.put(Model.PROP_EMAIL, email);
		
		String userRepoName = "usr://" + userName;
		
		Repository userRepo = repositoryDAO.addRepository(userRepoName, userName, conentStoreName);
		
		Repository repository = getPersonRepository();
		
		Entity entity = entityDAO.addEntity(repository, repository.getRootEntity().getId(), 
				Model.SYS_CONTAINS, 
				UUID.generate(), 
				Model.TYPE_CONTENT_PERSON,
				userName, null, maps);
		
		if (quota>0) {
			Map<QName, Serializable> m = new HashMap<QName, Serializable>(4);
			m.put(Model.PROP_QUOTA, quota * 1024 * 1024 * 1024);
			m.put(Model.PROP_FILE_SIZE, 0L);
			m.put(Model.PROP_FILE_TOTAL, 0L);
			m.put(Model.PROP_FOLDER_TOTAL, 0L);
			
			entityDAO.updateEntityProperties(userRepo.getRootEntity(), m);
		}
		return entity;
	}

	public Entity getPeopleEntity(String userName) {
		Repository repository = getPersonRepository();
		Entity personEntity = entityDAO.getEntityByPath(repository.getRootEntity(), userName);

		if (personEntity==null && AuthenticationUtil.ADMIN.equals(userName)) {
			personEntity = createPerson(AuthenticationUtil.ADMIN, adminPassword, -1, true, null, null, null);
		}
		
		return personEntity;
	}

	public void deletePerson(String userName) {
		
	}

	public Set<String> getAllPeople() {
		return null;
	}

	public Map<QName, Serializable> getPersonProperties(String userName) {
		return null;
	}

	@RestService(method="POST", uri="/person/checkpassword")
	public boolean checkPassword(@RestParam(value="name")String userName,
			@RestParam(value="password")String password) {
		
		Entity personEntity = getPeopleEntity(userName);
		
		if (personEntity==null) {
			return false;
		} else {
			Object got = personEntity.getProperty(Model.PROP_PASSWORD);
			return password.equals(personEntity.getProperty(Model.PROP_PASSWORD));
		}
		
	}
	
}
