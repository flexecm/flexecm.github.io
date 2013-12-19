package com.ever365.ecm.service.listener;

import java.io.InputStream;

import com.ever365.ecm.entity.Entity;

public class LogListener implements RepositoryListener {

	@Override
	public void beforeFileUpload(String repository, String path, String name,
			InputStream is, long size) {
		
	}	

	@Override
	public void onFileUploaded(Entity entity) {
		
	}

	@Override
	public void onFolderCreated(Entity entity) {
		
	}

	@Override
	public void onMoved(Entity srcEntity, Entity targetEntity) {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setEnabled(boolean enable) {

	}

	@Override
	public boolean enabled() {
		return false;
	}

	@Override
	public void onCopied(Entity srcEntity, Entity targetEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleted(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecovered(Entity entity) {
		// TODO Auto-generated method stub
		
	}

}
