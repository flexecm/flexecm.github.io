package com.ever365.ecm.clipboard;

import java.util.List;
import java.util.Map;

/**
 * operations for clipboard like  add\remove\list 
 * @author liuhan
 */
public interface ClipboardDAO {
	
	public void add(String info);
	
	public void remove(String info);
	
	public void clear();

	public List<Map<String, Object>> list();
}
