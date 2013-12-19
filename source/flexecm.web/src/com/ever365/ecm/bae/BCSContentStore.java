package com.ever365.ecm.bae;

import java.io.InputStream;
import java.util.logging.Logger;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.ever365.ecm.content.ContentStore;
import com.ever365.ecm.service.RepositoryService;
import com.ever365.utils.UUID;

public class BCSContentStore implements ContentStore {
	private static Logger logger = Logger.getLogger(BCSContentStore.class.getName());
	
	private String appKey;
	private String appSecret;
	private String bcsHost;
	private String bucketName;

	private BaiduBCS baiduBCS;
	
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
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, contentUrl);
		BaiduBCSResponse<DownloadObject> result = baiduBCS.getObject(getObjectRequest);
		return result.getResult().getContent();
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
		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType("application/octet-stream");
			objectMetadata.setContentLength(size);
			String object = "/" + UUID.generate();
			
			PutObjectRequest request = new PutObjectRequest(bucketName, object, inputStream, objectMetadata);
			ObjectMetadata result = baiduBCS.putObject(request).getResult();
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String putContent(String contentId, InputStream inputStream,
			long offset, long length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setContentUrl(String storeUrl) {
		
		try {
			String[] segs = storeUrl.split(":");
			bcsHost = segs[0];
			bucketName = segs[1];
			appKey = segs[2];
			appSecret = segs[3];
			
			BCSCredentials credentials = new BCSCredentials(appKey, appSecret);
			baiduBCS = new BaiduBCS(credentials, bcsHost);
			baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
			
		} catch (Exception e) {
			throw new RuntimeException();
		}
		
	}

}
