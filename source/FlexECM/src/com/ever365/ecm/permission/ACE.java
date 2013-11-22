package com.ever365.ecm.permission;

import com.ever365.ecm.entity.Entity;

/**
 * Access Control Entry
 * @author LiuHan
 *
 */

public class ACE {

	private String authority;
	private String entity;
	private String permission;
	private boolean allow;
	private String from;
	
	
	private String id;
	
	
	public ACE() {
		super();
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public boolean isAllow() {
		return allow;
	}
	public void setAllow(boolean allow) {
		this.allow = allow;
	}
	
	
}
