package com.ever365.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
	
	public static <V, K> Map<K, V> newMap(K k, V v) {
		Map<K, V> m = new HashMap<K, V>(1);
		m.put(k, v);
		return m;
	}
	
	
	public static <K,X> String get(Map<K, X> map, K k) {
		
		if (map.get(k)==null) {
			return null;
		} else {
			X v = map.get(k);
			return v.toString(); 
		}
	}
}
