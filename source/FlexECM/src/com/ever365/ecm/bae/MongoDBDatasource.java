package com.ever365.ecm.bae;

import java.util.Arrays;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBDatasource {
	private static Logger logger = Logger. getLogger(MongoDBDatasource.class.getName());
	
	private DB mydb = null;

	private String host;
	private String port;
	private String username;
	private String password;
	
	private String databaseName;
	
	
	public DB getMydb() {
		return mydb;
	}

	public void setMydb(DB mydb) {
		this.mydb = mydb;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public DB getApplicationDB() {
		if (mydb==null) {
			try {
				/*****1. 替换为你自己的数据库名（可从管理中心查看到）*****/
				/******2. 从环境变量里取出数据库连接需要的参数******/
				//String host = "mongo.duapp.com";//BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_MONGO_IP);
				//String port = "8908"; //BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_MONGO_PORT);
				//String username = "qkK7tDxwAovoRzX1CUb0CL0S"; //BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_AK);
				//String password = "sKmZsG3wdeg9WANqhZjrYYt5ASkRnw4G"; //BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_SK);
				
				//logger.info("host: " + host + " port:" + port + "  " + username + "   " + password);
				
				String serverName = host + ":" + port;
				
				/******3. 接着连接并选择数据库名为databaseName的服务器******/
				MongoClient mongoClient = new MongoClient(new ServerAddress(serverName),
						Arrays.asList(MongoCredential.createMongoCRCredential(username, databaseName, password.toCharArray())),
						new MongoClientOptions.Builder().cursorFinalizerEnabled(false).build());
				DB mongoDB = mongoClient.getDB(databaseName);
				mongoDB.authenticate(username, password.toCharArray());
				mydb = mongoDB;
			} catch (Exception e) {
				logger.info(extractError(e));
			}
		}
		
		//logger.info("get database=" + databaseName + " mydb= " + mydb);
		return mydb;
	}
	
	public static String extractError(Exception e) {
		StackTraceElement[] trances = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < trances.length; i++) {
			sb.append(trances[i].getClassName() + "  " + trances[i].getLineNumber() + "\n");
		}
		sb.append(e.getMessage());
		return sb.toString();
	}
	
	
}
