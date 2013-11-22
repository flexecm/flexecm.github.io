package com.ever365.ecm.content;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LiuHan
 */
public class ContentStoreFactory {
	
	private String defaultStoreUrl;
	private ContentStore defaultStore;
	
	public static final String DEFAULT_STORE_URL = "fs://../data";
	
	private Map<String, Class> contentStores;
	
	public ContentStoreFactory() {
		super();
		contentStores = new HashMap<String, Class>();
		
		contentStores.put("fs", FSContentStore.class);
	}
	public ContentStore getContentStore(String storeUrl) {
		String protocol = storeUrl.substring(0, storeUrl.indexOf("://"));
		
		Class<ContentStore> clazz = contentStores.get(protocol);
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
	
	public String getDefaultStoreUrl() {
		return defaultStoreUrl;
	}

	public void setDefaultStoreUrl(String defaultStoreUrl) {
		this.defaultStoreUrl = defaultStoreUrl;
	}

	
	public ContentStore getDefaultContentStore() {
		
		if (defaultStore==null) {
			if (defaultStoreUrl!=null) {
				defaultStore = getContentStore(defaultStoreUrl);
			} else {
				defaultStore = getContentStore(DEFAULT_STORE_URL);
			}
		}
		
		return defaultStore;
	}
}
