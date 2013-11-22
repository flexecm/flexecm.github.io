package com.ever365.ecm.log;

import java.util.List;
import java.util.Map;

/**
 * Log all the write and read operations for other services  
 * @author LiuHan
 */
public interface LogDAO {

	void addLog(Long time, String user, String entityId, String type, String msg);
	
	List<Map<String, Object>> filter(Map<String, Object> map, Integer size);
	
}
