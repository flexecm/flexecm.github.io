package com.ever365.mongo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;


/**
 * 
 * @author Administrator
 *
 */

public class LocalMongoDataSource implements MongoDataSource {

	private Map<String, DB> dbconnections = new HashMap<String, DB>();
	
	private String db;
	
	private String host;
	private String port;
	private String username;
	private String password;
	
	private int connectionPerhost = 10;
	
	public void setConnectionPerhost(int connectionPerhost) {
		this.connectionPerhost = connectionPerhost;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDb(String db) {
		this.db = db;
	}

	@Override
	public DBCollection getCollection(String name) {
		if (db==null) {
			db = "ever365db";
		}
		return getCollection(db, name);
	}

	@Override
	public DBCollection getCollection(String dbName, String collName) {
		if (dbconnections.get(dbName)==null) {
			synchronized (this) {
				if (dbconnections.get(dbName)==null) {
					try {
						if (host==null) {
							host = "127.0.0.1";
						} 
						if (port==null) {
							port = "27017";
						}
						String serverName = host + ":" + port;
						
						MongoClient mongoClient;
						MongoClientOptions mo = new MongoClientOptions.Builder().connectionsPerHost(connectionPerhost).build();
						
						if (username!=null && password!=null) {
							MongoCredential cred = MongoCredential.createMongoCRCredential(username, dbName, password.toCharArray());
							mongoClient = new MongoClient(new ServerAddress(serverName), Arrays.asList(cred), mo);
							DB mongoDB = mongoClient.getDB(dbName);
							mongoDB.authenticate(username, password.toCharArray());
							dbconnections.put(dbName, mongoDB);
						} else {
							mongoClient = new MongoClient(serverName, mo);
							dbconnections.put(dbName, mongoClient.getDB(dbName));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return dbconnections.get(dbName).getCollection(collName);
	}

	@Override
	public void clean() {
		Set<String> allcollections = dbconnections.get(db).getCollectionNames();
		for (String collection : allcollections) {
			if (collection.startsWith("system.")) continue;
			getCollection(collection).drop();
		}
	}

}
