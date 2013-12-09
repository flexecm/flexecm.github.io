package com.ever365.ecm.content;

import java.util.List;

import org.bson.types.ObjectId;

import com.ever365.ecm.bae.MongoDBDatasource;
import com.ever365.mongo.MongoDataSource;
import com.ever365.utils.MapUtils;
import com.ever365.utils.UUID;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class ContentDAOImpl implements ContentDAO {
	
	private MongoDataSource dataSource;
	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public String createContentData(String contentUrl, String mimetype,
			long size, String encoding) {
		DBCollection contents = getContentCollection();
		
		DBObject dbo = new BasicDBObject();
		String uuid = UUID.generate();
		dbo.put("uuid", uuid);
		dbo.put("url", contentUrl);
		dbo.put("mt", mimetype);
		dbo.put("size", size);
		dbo.put("enc", encoding);
		dbo.put("ref", 1);
		dbo.put("modified", System.currentTimeMillis());
		contents.insert(dbo);
		return dbo.get("_id").toString();
	}
	
	public DBCollection getContentCollection() {
		DBCollection contents = dataSource.getCollection("contents");
		return contents;
	}

	@Override
	public void copyContentData(String uuid) {
		DBObject dbo = new BasicDBObject();
		
		DBCollection contents = getContentCollection();
		dbo.put("$inc", MapUtils.newMap("ref", 1));
		
		contents.update(new BasicDBObject("_id", new ObjectId(uuid)), dbo);
	}

	@Override
	public void deleteContentData(String uuid) {
		DBObject dbo = new BasicDBObject();
		
		DBCollection contents = getContentCollection();
		dbo.put("$inc", MapUtils.newMap("ref", -1));
		
		contents.update(new BasicDBObject("_id", new ObjectId(uuid)), dbo);
	}

	@Override
	public String updateContentData(String uuid, String contentUrl,
			String mimetype, long size, String encoding) {
		return null;
	}

	@Override
	public List<String> getNotUsed() {
		return null;
	}

	@Override
	public ContentData getContentData(String id) {
		DBObject dbo = new BasicDBObject();
		dbo.put("_id", new ObjectId(id));
		
		DBObject contentdbo = getContentCollection().findOne(dbo);
		if (contentdbo!=null) {
			ContentData cd = new ContentData(null, (String)contentdbo.get("enc"), (String)contentdbo.get("mt"), 
					(String)contentdbo.get("url"));
			cd.setLength(((Long)contentdbo.get("size")).intValue());
			cd.setLastModified((Long)contentdbo.get("modified"));
			return cd;
		}
		
		return null;
	}

}
