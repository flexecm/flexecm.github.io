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
import com.ever365.utils.EmailUtils;
import com.ever365.utils.UUID;

/**
 * Simple implements of person service which use a flat store 
 * @author LiuHan
 */

public class PersonService {

	private EntityDAO entityDAO;
	private RepositoryDAO repositoryDAO;
	
	private String repoName;
	
	public static final String ADMIN = "admin";
	public static final String ADMIN_PASSWORD = "flexecm";

	public void setRepositoryDAO(RepositoryDAO repositoryDAO) {
		this.repositoryDAO = repositoryDAO;
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
			repoName = Repository.REPOSITORY_PERSON;
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
	
	@RestService(method="GET", uri="/person/properties")
	public Map<String, Object> getPersonProperties() {
		Entity personEntity = getPeopleEntity(AuthenticationUtil.getCurrentUser());
		return personEntity.toMap();
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


	@RestService(method="GET", uri="/person/current")
	public Map<String, Object> getCurrentPerson() {
		Entity user = getPeopleEntity(AuthenticationUtil.getCurrentUser());
		
		Map<String, Object> m = user.toMap();
		m.remove(Model.PROP_PASSWORD.getLocalName());
		return m;
	}
	
	
	@RestService(method="POST", uri="/password/modify")
	public void modifyPassword(@RestParam(value="old",required=true)String old,
			@RestParam(value="new", required=true)String newpass) {
		Entity personEntity = getPeopleEntity(AuthenticationUtil.getCurrentUser());
		if (personEntity==null) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		}
		
		if (!old.equals(personEntity.getProperty(Model.PROP_PASSWORD))) {
			throw new HttpStatusException(HttpStatus.FORBIDDEN);
		}
		Map<QName, Serializable> maps = new HashMap<QName, Serializable>();
		maps.put(Model.PROP_PASSWORD, newpass);
		entityDAO.updateEntityProperties(personEntity, maps);
	}
	
	@RestService(method="POST", uri="/person/modify")
	public void updatePerson(@RestParam(value="userId") String name,
			@RestParam(value="email") String email,
			@RestParam(value="password") String pwd
			)  {
		Entity p = getPeopleEntity(name);
		if (p==null) return;
		
		Map<QName, Serializable> maps = new HashMap<QName, Serializable>();
		if (email!=null && !email.equals("")) {
			maps.put(Model.PROP_EMAIL, email);
			sendAdminNotifyEmail(email, "您的flexecm账号更改", "您好,\n 您的flexecm账号: " + name + ", 密码更改为:" + pwd
					+ "\n请妥善保存，勿泄露给他人"
					);
		}
		
		if (pwd!=null && !pwd.equals("")) {
			maps.put(Model.PROP_PASSWORD, pwd);
		}
		entityDAO.updateEntityProperties(p, maps);
	}
	
	public void sendAdminNotifyEmail(String to, String title, String msg) {
		Entity adminEntity = getPeopleEntity(PersonService.ADMIN);
		if (Boolean.TRUE.equals(adminEntity.getProperty(Model.PROP_ADMIN_EMAIL_OK))) {
			try {
				EmailUtils.sendEmail(adminEntity.getPropertyStr(Model.PROP_ADMIN_SMTP), 
						adminEntity.getPropertyStr(Model.PROP_ADMIN_PORT),
						adminEntity.getPropertyStr(Model.PROP_ADMIN_EMAIL),
						adminEntity.getPropertyStr(Model.PROP_ADMIN_SMTP_PASS),
						adminEntity.getPropertyStr(Model.PROP_ADMIN_EMAIL),
						title, msg, to);
			} catch (Exception e) {
				
			}
		}
	}
	
	
	@RestService(method="POST", uri="/person/add")
	public Entity createPerson(@RestParam(value="userId")String userName,
			@RestParam(value="password")String password,
			@RestParam(value="quota")Long quota,
			@RestParam(value="admin")Boolean admin,
			@RestParam(value="email")String email,
			@RestParam(value="dept")String dept, @RestParam(value="store")String conentStoreName
	) {
		Map<QName, Serializable> maps = new HashMap<QName, Serializable>();
		
		maps.put(Model.PROP_PASSWORD, password);
		
		if (email!=null && !email.equals("")) {
			maps.put(Model.PROP_EMAIL, email);
			sendAdminNotifyEmail(email, "您的flexecm账号", "您好,\n您的flexecm账号: " + userName + ", 密码为:" + password
					+ "\n请妥善保存，勿泄露给他人"
					);
		}
		
		maps.put(Model.PROP_EMAIL, email);
		
		
		String userRepoName = Repository.PROTOCOL_USR + Repository.URI_FILLER + userName;
		
		Repository userRepo = repositoryDAO.addRepository(userRepoName, userName, conentStoreName);
		
		Repository repository = getPersonRepository();
		
		Entity entity = entityDAO.addEntity(repository, repository.getRootEntity().getId(), 
				Model.SYS_CONTAINS, 
				UUID.generate(), 
				Model.TYPE_CONTENT_PERSON,
				userName, null, maps);
		
		Map<QName, Serializable> m = new HashMap<QName, Serializable>(4);
		if (quota>0) {
			m.put(Model.PROP_QUOTA, quota * 1024 * 1024 * 1024);
		}
		m.put(Model.PROP_FILE_SIZE, 0L);
		m.put(Model.PROP_FILE_TOTAL, 0L);
		m.put(Model.PROP_FOLDER_TOTAL, 0L);
		entityDAO.updateEntityProperties(userRepo.getRootEntity(), m);
		return entity;
	}
	
	
	@RestService(method="POST", uri="/person/email") 
	public void setPersonEmail(@RestParam(value="email")String email,
			@RestParam(value="smtp")String smtp,
			@RestParam(value="port")String smtpport,
			@RestParam(value="emailpass")String smtppass) {
		Entity peopleEntity = getPeopleEntity(AuthenticationUtil.getCurrentUser());
		
		Map<QName, Serializable> maps = new HashMap<QName, Serializable>();
		maps.put(Model.PROP_ADMIN_SMTP, smtp);
		maps.put(Model.PROP_ADMIN_PORT, smtpport);
		maps.put(Model.PROP_ADMIN_SMTP_PASS, smtppass);
		maps.put(Model.PROP_ADMIN_EMAIL, email);

		try {
			EmailUtils.sendEmail(smtp, smtpport, email, smtppass, email, "hi flexecm", "hi, I am using flex ecm", "liuhan@ever365.com");
			maps.put(Model.PROP_ADMIN_EMAIL_OK, true);
		} catch (Throwable t) {
			maps.put(Model.PROP_ADMIN_EMAIL_OK, false);
		}
		entityDAO.updateEntityProperties(peopleEntity, maps);
	}
	
	
	
	@RestService(method="POST", uri="/person/remove")
	public void removePerson(@RestParam(value="id")String userName) {
		Entity peppleEntity = getPeopleEntity(userName);
		
		List<Repository> repos = repositoryDAO.getRepositoriesByOwner(userName);
		
		for (Repository repository : repos) {
			entityDAO.deleteEntity(repository.getRootEntity());
			entityDAO.deleteEntity(repository.getTrashEntity());
			repositoryDAO.dropRepository(repository.toString());
		}
		entityDAO.deleteEntity(peppleEntity);
	}

	public Entity getPeopleEntity(String userName) {
		Repository repository = getPersonRepository();
		Entity personEntity = entityDAO.getEntityByPath(repository.getRootEntity(), userName);
		
		if (personEntity==null && ADMIN.equals(userName)) {
			personEntity = createPerson(ADMIN, ADMIN_PASSWORD, -1L, true, null, null, null);
			repositoryDAO.addRepository(Repository.REPOSITORY_DEFAULT, ADMIN, null);
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
