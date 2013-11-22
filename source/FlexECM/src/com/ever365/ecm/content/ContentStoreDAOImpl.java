package com.ever365.ecm.content;

import java.util.List;

import com.ever365.mongo.MongoDataSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author LiuHan
 */

public class ContentStoreDAOImpl implements ContentStoreDAO {

	private ContentStoreFactory contentStoreFactory;

	private MongoDataSource dataSource;
	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setContentStoreFactory(ContentStoreFactory contentStoreFactory) {
		this.contentStoreFactory = contentStoreFactory;
	}

	@Override
	public ContentStore getContentStore(String name) {
		if (name==null) {
			return contentStoreFactory.getDefaultContentStore();
		} else {
			DBObject dbo = dataSource.getCollection("store").findOne(new BasicDBObject("name", name));
			
			String storeUrl = (String)dbo.get("url");
			
			return contentStoreFactory.getContentStore(storeUrl);
		}
	}

	@Override
	public void addContentStore(String name, String contentStoreUrl) {
	
		DBObject dbo = new BasicDBObject();
		dbo.put("name", name);
		dbo.put("url", contentStoreUrl);
		
		dataSource.getCollection("store").insert(dbo);
	}

	@Override
	public List<ContentStore> getAllContentStores() {
		
		return null;
	}

	@Override
	public void removeContentStore(String name) {

	}

}
