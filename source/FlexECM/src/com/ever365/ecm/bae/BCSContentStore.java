package com.ever365.ecm.bae;

import java.io.InputStream;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.ever365.ecm.content.ContentData;
import com.ever365.ecm.content.ContentStore;
import com.ever365.utils.UUID;

public class BCSContentStore implements ContentStore {

	private String appKey;
	private String appSecret;
	private String bcsHost;
	private String bucketName;
	
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public void setBcsHost(String bcsHost) {
		this.bcsHost = bcsHost;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	public String putContent(InputStream inputStream,
			String contentType, long size) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(contentType);
		objectMetadata.setContentLength(size);
		String object = "/" + UUID.generate();
		
		BCSCredentials credentials = new BCSCredentials(appKey, appSecret);
		BaiduBCS baiduBCS = new BaiduBCS(credentials, bcsHost);
		baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
		
		PutObjectRequest request = new PutObjectRequest(bucketName, object, inputStream, objectMetadata);
		ObjectMetadata result = baiduBCS.putObject(request).getResult();
		return object;
	}
	@Override
	public long getSpaceUsed() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long getSpaceFree() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long getSpaceTotal() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getRootLocation() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean exists(String contentUrl) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean deleteContentData(String contentUrl) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public InputStream getContentData(String contentUrl) {
		return null;
	}
	@Override
	public String getStoreName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getStoreUrl() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String putContent(InputStream inputStream, long size) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("application/octet-stream");
		objectMetadata.setContentLength(size);
		String object = "/" + UUID.generate();
		
		BCSCredentials credentials = new BCSCredentials(appKey, appSecret);
		BaiduBCS baiduBCS = new BaiduBCS(credentials, bcsHost);
		baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
		
		PutObjectRequest request = new PutObjectRequest(bucketName, object, inputStream, objectMetadata);
		ObjectMetadata result = baiduBCS.putObject(request).getResult();
		return object;
	}
	@Override
	public String putContent(String contentId, InputStream inputStream,
			long offset, long length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setContentUrl(String storeUrl) {
		// TODO Auto-generated method stub
		
	}

}
