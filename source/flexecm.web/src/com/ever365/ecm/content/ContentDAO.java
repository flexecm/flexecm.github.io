package com.ever365.ecm.content;

import java.util.List;

/**
 * data for content location url and handle when the data is copied or removed; 
 * @author LiuHan
 */

public interface ContentDAO {

	String createContentData(String contentUrl, String mimetype, long size, String encoding);
	  
	void copyContentData(String uuid);
	  
	void deleteContentData(String uuid);
	  
	String updateContentData(String uuid, String contentUrl, String mimetype, long size, String encoding);
	
	ContentData getContentData(String contentUrl);
	
	List<String> getNotUsed();
	
}
