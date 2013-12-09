package com.ever365.ecm.content;

import java.util.HashMap;
import java.util.Map;

import com.ever365.ecm.authority.PersonService;
import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.repo.Model;

/**
 * @author LiuHan
 */
public class ContentStoreFactory {
	
	public static final String SPLITTER = "://";
	public static final String FS = "fs";
	private ContentStore defaultStore;
	private PersonService personService;
	
	public static final String DEFAULT_STORE_URL = "fs://../data";
	
	private Map<String, Class> contentStores;
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public ContentStoreFactory() {
		super();
		contentStores = new HashMap<String, Class>();
		contentStores.put(FS, FSContentStore.class);
	}
	
	
	public ContentStore getContentStore(String storeUrl) {
		if (storeUrl==null) return getDefaultContentStore(); 
		
		return initContentStore(storeUrl);
		
	}
	
	public ContentStore initContentStore(String storeUrl) {
		String protocol = FS;
		
		if (storeUrl.indexOf(SPLITTER)>-1) {
			protocol = storeUrl.substring(0, storeUrl.indexOf(SPLITTER));
			storeUrl = storeUrl.substring(storeUrl.indexOf(SPLITTER) + SPLITTER.length());
		}
		
		Class<ContentStore> clazz = contentStores.get(protocol);
		if (clazz==null) return null;
		
		try {
			ContentStore store = clazz.newInstance();
			store.setContentUrl(storeUrl);
			return store; 
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setDefaultStore(String url) {
		defaultStore = initContentStore(url);
	}
	
	
	public ContentStore getDefaultContentStore() {
		
		//first, get the store location from admin configurations.
		Entity adminEntity = personService.getPeopleEntity(PersonService.ADMIN);
		if (adminEntity!=null) {
			if (adminEntity.getProperty(Model.PROP_DEFAULT_STORE)!=null) {
				defaultStore = initContentStore((String)adminEntity.getProperty(Model.PROP_DEFAULT_STORE));
			}
		}
		
		//if not exist, load default location
		if (defaultStore==null) {
			defaultStore = initContentStore(DEFAULT_STORE_URL);
		}
		
		return defaultStore;
	}
}
