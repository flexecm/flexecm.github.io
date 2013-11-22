package com.ever365.ecm.permission;

import java.util.List;
import java.util.Map;

public interface AclDAO {
	
	public List<ACE> getACEs(String entityId);
	
	public void removeACE(String entityId, String aceid);
	
	public void removeACE(String entityId);
	
	public ACE addACE(String entityId, String authority, String permission, boolean allowed);
	
	public Map<String, List<Access>> getPermissionGroups(); 
	
	public List<ACE> findACEs(String source, String target); 
	
}
