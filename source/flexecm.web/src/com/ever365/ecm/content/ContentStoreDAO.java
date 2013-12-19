package com.ever365.ecm.content;

import java.util.List;

/**
 * @author LiuHan
 */
public interface ContentStoreDAO {
	/**
	 * Get the store by store name 
	 * @param name  if null, return default local store
	 * @return  ContentStore
	 */
	public ContentStore getContentStore(String name);
	
	/**
	 * @param name
	 * @param contentStoreUrl
	 */
	public void addContentStore(String name, String contentStoreUrl);
	
	/**
	 * @return
	 */
	public List<ContentStore> getAllContentStores();
	
	public void removeContentStore(String name);
}
