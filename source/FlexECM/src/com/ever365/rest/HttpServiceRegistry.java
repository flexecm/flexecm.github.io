package com.ever365.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Register all http url method 
 * @author Liu Han
 */
public class HttpServiceRegistry {
	public static final String METHOD_POST = "post";
	public static final String METHOD_GET = "get";

	//注册的POST方法
	private Map<String, MethodInvocation> posts = new HashMap<String, MethodInvocation>();
	
	//注册的GET方法
	private Map<String, MethodInvocation> gets  = new HashMap<String, MethodInvocation>();

	
	public MethodInvocation getGet(String getUri) {
		return gets.get(getUri);
	}
	public MethodInvocation getPost(String postUri) {
		return posts.get(postUri);
	}
	
	public void setInjectedResouce(Object object)  {

		Method[] methods = object.getClass().getMethods();
		for (Method method : methods) {
			
			if (method.getAnnotation(RestService.class)!=null) {
				RestService rs = method.getAnnotation(RestService.class);
				MethodInvocation mi = new MethodInvocation();
				mi.setMethod(method);
				mi.setService(object);
				mi.setTransactional(rs.transactional());
				mi.setRunAsAdmin(rs.runAsAdmin());
				mi.setMultipart(rs.multipart());
				mi.setUri(rs.uri());
				mi.setCached(rs.cached());
				mi.setCachePublic(rs.cachePublic());
				mi.setCacheExpire(rs.cacheExpire());
				mi.setUseDAOCache(rs.useDAOCache());
				Annotation[][] paramAnno = method.getParameterAnnotations();
				Class<?>[] paramTypes = method.getParameterTypes();
				for (int i = 0; i < paramAnno.length; i++) {
					if (paramAnno[i].length==1 && paramAnno[i][0] instanceof RestParam) {
						mi.pushParam(((RestParam)paramAnno[i][0]).value(), paramTypes[i]);
						mi.pushParamRequired(((RestParam)paramAnno[i][0]).value(), ((RestParam)paramAnno[i][0]).required());
					}
				}
				if (METHOD_POST.equalsIgnoreCase(rs.method()) && rs.uri()!=null && !rs.uri().equals("")) {
					MethodInvocation methodInvocation = posts.get(rs.uri());
					
					if( null!=methodInvocation ) {
						throw new RuntimeException("uri:"+ rs.uri() +"already been injected by other class ");
					}
					posts.put(rs.uri(), mi);
				}
				if (METHOD_GET.equalsIgnoreCase(rs.method()) && rs.uri()!=null && !rs.uri().equals("")) {
					MethodInvocation methodInvocation = gets.get(rs.uri());
					
					if( null!=methodInvocation ) {
						throw new RuntimeException("uri:"+ rs.uri() +"already been injected by other class ");
					}
					gets.put(rs.uri(), mi);
				}
			}
		}
	
	}
	
	public void setInjectedServices(List<Object> injectedServices)  {
		for (Object object : injectedServices) {
			setInjectedResouce( object);
		}
		
	}
	
}
