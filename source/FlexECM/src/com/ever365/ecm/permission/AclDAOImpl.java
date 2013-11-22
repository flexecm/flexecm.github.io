package com.ever365.ecm.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.ever365.ecm.authority.AuthenticationUtil;
import com.ever365.mongo.MongoDataSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Mongodb impl for acl  
 * @author Liu Han
 */

public class AclDAOImpl implements AclDAO {

	private Map<String, String> defaultGroups;
	
	private Map<String, Map<String, List<Access>>> groupCache = new HashMap<String, Map<String,List<Access>>>();

	private MongoDataSource dataSource;
	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setDefaultGroups(Map<String, String> defaultGroups) {
		this.defaultGroups = defaultGroups;
	}

	@Override
	public List<ACE> getACEs(String entityId) {
		DBCursor cursor = dataSource.getCollection("ace").find(new BasicDBObject("eid", new ObjectId(entityId)));
		List<ACE> result = new ArrayList<ACE>();
		try {
			while (cursor.hasNext()) {
				DBObject dbo = cursor.next();
				ACE ace = convertToACE(dbo);
				result.add(ace);
			}
		} finally {
			cursor.close();
		}
		
		return result;
	}

	public ACE convertToACE(DBObject dbo) {
		ACE ace = new ACE();
		ace.setId(dbo.get("_id").toString());
		ace.setAllow((Boolean)dbo.get("allow"));
		ace.setAuthority((String)dbo.get("auth"));
		ace.setPermission((String)dbo.get("per"));
		ace.setEntity(dbo.get("eid").toString());
		ace.setFrom((String)dbo.get("from"));
		return ace;
	}

	@Override
	public void removeACE(String entityId, String aceid) {
		
	}

	@Override
	public void removeACE(String entityId) {
		dataSource.getCollection("ace").remove(new BasicDBObject("eid", new ObjectId(entityId)));
	}

	@Override
	public ACE addACE(String entityId, String authority, String permission,
			boolean allowed) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("eid", new ObjectId(entityId));
		dbo.put("from", AuthenticationUtil.getCurrentUser());
		dbo.put("auth", authority);
		dbo.put("per", permission);
		dbo.put("allow", allowed);
		
		dataSource.getCollection("ace").insert(dbo);
		return convertToACE(dbo);
	}

	@Override
	public Map<String, List<Access>> getPermissionGroups() {
		
		if (groupCache.get(AuthenticationUtil.getTenant())==null) {
			synchronized (groupCache) {
				if (groupCache.get(AuthenticationUtil.getTenant())==null) {// double check
					DBCollection permissionGroupColl = dataSource.getCollection("group");
					
					DBCursor cursor = permissionGroupColl.find(new BasicDBObject("tenant", AuthenticationUtil.getTenant()));
					
					if (!cursor.hasNext()) { // init permission group
						for (String permissionGroup : defaultGroups.keySet()) {
							DBObject pgdbo = new BasicDBObject();
							pgdbo.put("groupName", permissionGroup);
							pgdbo.put("tenant", AuthenticationUtil.getTenant());
							pgdbo.put("permissions", defaultGroups.get(permissionGroup).split(","));
							permissionGroupColl.insert(pgdbo);
						}
					}
					
					cursor = permissionGroupColl.find(new BasicDBObject("tenant", AuthenticationUtil.getTenant()));
					
					Map<String, List<Access>> m = new HashMap<String, List<Access>>();
					while(cursor.hasNext()) {
						DBObject dbo = cursor.next();
						
						List<String> permissions = (List<String>)dbo.get("permissions");
						
						List<Access> access = new ArrayList<Access>(permissions.size());
						
						for (String permission: permissions) {
							for (Access a : Access.values()) {
								if (a.value()==Integer.valueOf(permission)) {
									access.add(a);
								}
							}
						}
						
						m.put((String)dbo.get("groupName"), access);
					}
					
					groupCache.put(AuthenticationUtil.getTenant(), m);
					
				} 
				
			}
		}
		
		return groupCache.get(AuthenticationUtil.getTenant());
	}
	
	public void init() {
	}

	@Override
	public List<ACE> findACEs(String source, String target) {
		
		DBObject dbo = new BasicDBObject();
		if (source!=null) {
			dbo.put("from", source);
		}
		if (target!=null) {
			dbo.put("auth", target);
		}
			
		DBCursor cursor = dataSource.getCollection("ace").find(dbo);
		
		List<ACE> result = new ArrayList<ACE>();
		while (cursor.hasNext()) {
			DBObject t = cursor.next();
			result.add(convertToACE(t));
		}
		return result;
	}
	

}
