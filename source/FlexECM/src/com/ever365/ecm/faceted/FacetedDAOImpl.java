package com.ever365.ecm.faceted;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ever365.mongo.MongoDataSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Faceted mongodb store impl 
 * @author LiuHan
 */
public class FacetedDAOImpl implements FacetedDAO {

	private MongoDataSource dataSource;

	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	private DBCollection getCollection() {
		return dataSource.getCollection("faceted");
	}
	
	@Override
	public void addFaceted(String parent, String title) {
		BasicDBObject dbo = new BasicDBObject();
		if (parent!=null) {
			dbo.put("title", parent + "/" + title);
		} else {
			dbo.put("title", title);
		}
		getCollection().update(dbo, dbo, true, false);
	}
	

	@Override
	public List<String> list() {
		
		DBCursor cursor = getCollection().find();
		
		LinkedList<String> result = new LinkedList<String>();
		
		while(cursor.hasNext()) {
			DBObject dbo = cursor.next();
			result.push((String)dbo.get("title"));
		}
		
		Collections.sort(result);
		return result;
	}

	@Override
	public List<String> getSubFaceteds(String parentTitle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFaceted(String title) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("title", title);
		getCollection().remove(dbo);

	}

}
