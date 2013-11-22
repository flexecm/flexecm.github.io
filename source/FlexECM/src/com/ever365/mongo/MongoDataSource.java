package com.ever365.mongo;

import com.mongodb.DBCollection;

/**
 * 
 * @author Interface for mongodb client operations
 *
 */
public interface MongoDataSource {

	public DBCollection getCollection(String name);
	
	public DBCollection getCollection(String dbName, String collName);
	
}
