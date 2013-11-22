package com.ever365.rest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RestService {
	String method();
	String uri();
	boolean transactional() default true;
	boolean runAsAdmin() default false;
	boolean multipart() default false;
	
	boolean cached() default false; //是否缓存
	boolean cachePublic() default false; //缓存是否关联当前用户，默认为所有用户用相同的缓存
	int cacheExpire() default 0;  //缓存是否失效  0为永不失效 
	
	boolean useDAOCache() default true; //是否使用DAO缓存
}
