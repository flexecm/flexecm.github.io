package com.ever365.ecm.log;

import java.util.List;
import java.util.Map;

import com.ever365.mongo.MongoDataSource;

/**
 * 
 * @author LiuHan
 */
public class LogDAOImpl implements LogDAO {
	private MongoDataSource dataSource;
	
	public MongoDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void addLog(Long time, String user, String entityId, String type,
			String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Map<String, Object>> filter(Map<String, Object> map,
			Integer size) {
		// TODO Auto-generated method stub
		return null;
	}

}
