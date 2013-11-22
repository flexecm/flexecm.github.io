package com.ever365.ecm.content;

import java.io.InputStream;

/**
 * wrapper for a content with stream,encoding, and mimeType ..etc
 *  @author Liu Han
 */

public class ContentData {

	private InputStream inputStream;
	private String encoding;
	private String mimeType;
	private String md5;
	
	private String contentUrl;

	public ContentData(InputStream inputStream, String encoding,
			String mimeType, String contentUrl) {
		super();
		this.inputStream = inputStream;
		this.encoding = encoding;
		this.mimeType = mimeType;
		this.contentUrl = contentUrl;
	}
	
	public String getMd5() {
		return md5;
	}
	
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
	
	
	
}
