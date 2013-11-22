package com.ever365.ecm.clipboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.ever365.ecm.authority.AuthenticationUtil;
import com.ever365.mongo.MongoDataSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class ClipboardDAOImpl implements ClipboardDAO {

	private MongoDataSource dataSource;
	
	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void add(String info) {
		DBObject dbo = new BasicDBObject();
		dbo.put("usr", AuthenticationUtil.getCurrentUser());
		dbo.put("info", info);
		
		dataSource.getCollection("clipboard").insert(dbo);
	}

	@Override
	public void remove(String info) {
		DBObject dbo = new BasicDBObject();
		dbo.put("usr", AuthenticationUtil.getCurrentUser());
		dbo.put("info", info);
		dataSource.getCollection("clipboard").remove(dbo);
	}

	@Override
	public void clear() {
		DBObject dbo = new BasicDBObject();
		dbo.put("usr", AuthenticationUtil.getCurrentUser());
		dataSource.getCollection("clipboard").remove(dbo);
	}

	@Override
	public List<Map<String, Object>> list() {
		DBObject dbo = new BasicDBObject();
		dbo.put("usr", AuthenticationUtil.getCurrentUser());
		DBCursor cursor = dataSource.getCollection("clipboard").find(dbo);
		
		
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		while (cursor.hasNext()) {
			DBObject o = cursor.next();
			Map<String, Object> i = new HashMap<String, Object>();
			i.put("info", o.get("info"));
			i.put("created", ((ObjectId)o.get("_id")).getTime());
			
			result.add(i);
		}
		
		return result;
	}

}
