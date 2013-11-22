package com.ever365.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * 执行一个方法调用，并处理相关的参数转换 
 * @author 刘晗
 */
public class MethodInvocation {
	
	private static final String NULL = "null";
	private Method method;
	private String uri;
	private Object service;
	private boolean transactional;
	private boolean runAsAdmin;
	private boolean multipart;
	
	private boolean cached;
	private boolean cachePublic;
	private int cacheExpire;
	private boolean useDAOCache;
	
	public boolean useDAOCache() {
		return useDAOCache;
	}

	public void setUseDAOCache(boolean useDAOCache) {
		this.useDAOCache = useDAOCache;
	}

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public boolean isCachePublic() {
		return cachePublic;
	}

	public void setCachePublic(boolean cachePublic) {
		this.cachePublic = cachePublic;
	}

	public int getCacheExpire() {
		return cacheExpire;
	}

	public void setCacheExpire(int cacheExpire) {
		this.cacheExpire = cacheExpire;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}

	private LinkedHashMap<String, Class> paramsMap = new LinkedHashMap<String, Class>();
	private LinkedHashMap<String, Boolean> paramsRequired = new LinkedHashMap<String, Boolean>();
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	public Method getMethod() {
		return method;
	}
	
	public void pushParam(String name, Class type) {
		paramsMap.put(name, type);
	}
	public void pushParamRequired(String name, boolean required) {
		if (required) {
			paramsRequired.put(name, required);
		}
	}
	
	public Object execute(Map<String,Object> map) {
		
		Object[] methodParams = getMethodParams(map);
		try {
			return method.invoke(service, methodParams);
		} catch (IllegalArgumentException e) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		} catch (IllegalAccessException e) {
			throw new HttpStatusException(HttpStatus.METHOD_FAILURE);
		} catch (InvocationTargetException e) {
			if (e.getTargetException()!=null && (e.getTargetException() instanceof RuntimeException)) {
				throw (RuntimeException) e.getTargetException();
			} 
		}
		return null;
	}

	public Object[] getMethodParams(Map<String, Object> map) {
		Set<String> requiredParams = paramsRequired.keySet();
		for (String key : requiredParams) {
			if (map.get(key)==null) {
				throw new HttpStatusException(HttpStatus.BAD_REQUEST);
			}
		}
		
		Set<Entry<String, Class>> es = paramsMap.entrySet();
		Object[] methodParams = new Object[es.size()];
		int i=0;
		for (Entry<String, Class> entry : es) {
			methodParams[i] = convert(entry.getValue(), map.get(entry.getKey()));
			i ++;
		}
		return methodParams;
	}
	
	public Object convert(Class clazz, Object obj) {
		if (obj==null || NULL.equals(obj))  {
			if (clazz.equals(boolean.class)) {
				return Boolean.FALSE;
			}
			if (clazz.equals(Integer.class)) {
				return 0;
			}
			if (clazz.equals(Long.class)) {
				return 0L;
			}
			return null;
		}
		
		if (clazz.isInstance(obj)) {
			return obj;
		}
		
		if (clazz.getName().equals("int") || clazz==Integer.class) {
			return new Integer(obj.toString());
		}
		
		if (clazz.getName().equals("long") || clazz==Long.class) {
			return new Long(obj.toString());
		}
		
 		
		if (clazz==List.class && obj instanceof String) {
			try {
				JSONArray jsa = new JSONArray((String)obj);
				return jsonArrayToList(jsa);
			} catch (JSONException e) {
				return null;
			}
		}
		
		if (clazz==Date.class && obj instanceof String) {
			try {
				return dateformat.parse((String) obj);
			} catch (ParseException e) {
				throw new HttpStatusException(HttpStatus.BAD_REQUEST);
			}	
		}
		
		if (clazz==Map.class && obj instanceof String) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject((String)obj);
				return jsonObjectToMap(jsonObject);
			} catch (org.json.JSONException e) {
				return null;
			}
		}
		
		if(clazz==Boolean.class && obj instanceof String) {
			return Boolean.valueOf((String)obj);
		}
		
		
		throw new HttpStatusException(HttpStatus.BAD_REQUEST);
	}
	
	
	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getService() {
		return service;
	}

	public void setService(Object service) {
		this.service = service;
	}

	public boolean isRunAsAdmin() {
		return runAsAdmin;
	}

	public void setRunAsAdmin(boolean runAsAdmin) {
		this.runAsAdmin = runAsAdmin;
	}

	public boolean isMultipart() {
		return multipart;
	}

	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public static List<Object> jsonArrayToList(org.json.JSONArray jsonArray) {
		List<Object> ret = new ArrayList<Object>();
		Object value = null;
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			try {
				value = jsonArray.get(i);
			} catch (JSONException e) {
				System.out.println(" there are no value with the index in the JSONArray");
				e.printStackTrace();
				return null;
			}
			if (value instanceof JSONArray) {
				ret.add(jsonArrayToList((JSONArray) value));
			} else if (value instanceof JSONObject) {
				ret.add(jsonObjectToMap((JSONObject) value));
			} else {
				ret.add(value);
			}
		}

		return (ret.size() != 0) ? ret : null;
	}

	public static Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Object value = null;
		String key = null;
		for (Iterator<?> keys = jsonObject.keys(); keys.hasNext();) {
			key = (String) keys.next();
			try {
				value = jsonObject.get(key);
			} catch (JSONException e) {
				System.out.println("the key is not found in the JSONObject");
				e.printStackTrace();
				return null;
			}
			if (value instanceof JSONArray) {
				ret.put(key, jsonArrayToList((JSONArray) value));
			} else if (value instanceof JSONObject) {
				ret.put(key, jsonObjectToMap((JSONObject) value));
			} else {
				ret.put(key, value);
			}

		}

		return ret.size() != 0 ? ret : null;

	}
	
}
