package com.ever365.utils;

public class StringUtils {

	public static final Long tofileSize(String v) {
		try {
			if (v.endsWith("G")|| v.endsWith("g")) {
				Long value = Long.parseLong(v.substring(0,v.length()-1));
				return value * 1024 * 1024 * 1024;
			}
			
			if (v.endsWith("M")|| v.endsWith("m")) {
				Long value = Long.parseLong(v.substring(0,v.length()-1));
				return value * 1024 * 1024;
			}
			
			if (v.endsWith("K")|| v.endsWith("k")) {
				Long value = Long.parseLong(v.substring(0,v.length()-1));
				return value * 1024;
			}
			return Long.parseLong(v);
		} catch (Exception e) {
			return 0L;
		}
	}
	
}
