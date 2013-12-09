package com.ever365.ecm.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ever365.ecm.authority.PersonService;
import com.ever365.ecm.content.FSContentStore;
import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.entity.EntityDAO;
import com.ever365.ecm.repo.Model;
import com.ever365.ecm.repo.QName;
import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;
import com.ever365.utils.EmailUtils;

/**
 * 
 * @author LiuHan
 */
public class ConfigService {
	
	private MongoDataSource dataSource;
	private PersonService personService;
	
	private EntityDAO entityDAO;
	public static final String  VERSION = "1.0.1";
	
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public MongoDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	@RestService(method="GET", uri="/reset", runAsAdmin=true)
	public Map<String, Object> reset(@RestParam(value="pw")String pw) {
		if (!personService.checkPassword(PersonService.ADMIN, pw)) {
			throw new HttpStatusException(HttpStatus.FORBIDDEN);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		dataSource.clean();
		result.put("msg", "ok");
		return result;
	}
	
	@RestService(method="POST", uri="/init", runAsAdmin=true)
	public Map<String, Object> bootStrapInit(@RestParam(value="pw")String pw,
			@RestParam(value="path")String path,
			@RestParam(value="email")String email,
			@RestParam(value="smtp")String smtp,
			@RestParam(value="port")String smtpport,
			@RestParam(value="emailpass")String smtppass
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		Entity adminEntity = personService.getPerson(PersonService.ADMIN);
		
		if (adminEntity.getProperty(Model.PROP_DEFAULT_STORE)!=null) {
			result.put("msg", "initialized");
			return result;
		}
		
		if (!FSContentStore.checkRootDir(path)) {
			result.put("msg", "666");
			return result;
		}
		
		Map<QName, Serializable> specialProperties = new HashMap<QName, Serializable>();
		specialProperties.put(Model.PROP_DEFAULT_STORE, path);
		specialProperties.put(Model.PROP_PASSWORD, pw);
		entityDAO.updateEntityProperties(adminEntity, specialProperties);
		
		personService.setPersonEmail(email, smtp, smtpport, smtppass);
		
		result.put("msg", "ok");
		result.putAll(getAdminConfig());
		return result;
	}
	
	
	@RestService(method="GET", uri="/admin/config", runAsAdmin=true)
	public Map<String, Object> getAdminConfig() {
		Entity adminEntity = personService.getPerson(PersonService.ADMIN);
		Map<String, Object> map = adminEntity.toMap();
		map.put("version", VERSION);
		
		
		Map<String, String> envs = System.getenv();
		for (String key : envs.keySet()) {
			map.put(key, envs.get(key));
		}
		
		Properties props = System.getProperties();
		for (Object key : props.keySet()) {
			map.put(key.toString(), props.get(key));
		}
		
		
		
		return map;
		
	}
	
	
	
	@RestService(method="POST", uri="/admin/config", runAsAdmin=true)
	public void setAdminConfig(@RestParam(value="defaultStore")String defaultStore,
			@RestParam(value="smtp")String smtp,
			@RestParam(value="smtpport")String smtpport,
			@RestParam(value="email")String email,
			@RestParam(value="smtppass")String smtppass
			) {
		Entity adminEntity = personService.getPerson(PersonService.ADMIN);
		if (adminEntity==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		Map<QName, Serializable> maps = new HashMap<QName, Serializable>();
		if (defaultStore!=null) {
			maps.put(Model.PROP_DEFAULT_STORE, defaultStore);
		}
		
		if (smtp!=null) {
			EmailUtils.check(smtp, smtpport, email, smtppass);
			maps.put(Model.PROP_ADMIN_SMTP, smtp);
			maps.put(Model.PROP_ADMIN_PORT, smtpport);
			maps.put(Model.PROP_ADMIN_SMTP_PASS, smtppass);
			maps.put(Model.PROP_ADMIN_EMAIL, email);
			entityDAO.updateEntityProperties(adminEntity, maps);
		}
	}
	
	

}
