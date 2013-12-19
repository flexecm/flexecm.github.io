package com.ever365.ecm.authority;

import com.ever365.ecm.entity.Entity;

public interface GroupService {
	
	public Entity createGroup(String parent,  String name);
	
	public Entity addUser(Entity group,  String userName);
	
}
