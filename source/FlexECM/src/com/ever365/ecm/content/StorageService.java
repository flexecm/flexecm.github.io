package com.ever365.ecm.content;

import com.ever365.ecm.authority.PersonService;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;

public class StorageService {

	private ContentStoreFactory contentStoreFactory;
	private PersonService personService;
	
	@RestService(method="GET", uri="/store/default", runAsAdmin=true)
	public String getDefaultStore() {
		return contentStoreFactory.getDefaultContentStore().getRootLocation();
	}
	
}
