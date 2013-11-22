package com.ever365.ecm.permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ever365.ecm.authority.AuthenticationUtil;
import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.entity.EntityDAO;
import com.ever365.ecm.repo.Model;
import com.ever365.ecm.repo.QName;

/**
 * ACE  Access Control Entry: the access details  permission_id, authority_id, allowed,   
 * ACL  Access Control List:   acl_id, inherit_from
 * ace is 1:n with aclmember
 * acl is n:1  to entry or repository
 * 
 * @author Liu Han
 */

public class PermissionService {

	private EntityDAO entityDAO;
	private AclDAO aclDAO;

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}
	
	public void setAclDAO(AclDAO aclDAO) {
		this.aclDAO = aclDAO;
	}
	
	
	/**
	 * Get the permission which take effect
	 * @param entity
	 * @return
	 */
	public List<ACE> getPermissions(Entity entity) {
		List<ACE> list = getAllSetPermissions(entity, false);
		
		if (entity.getInheritAcl() && entity.getParentId()!=null) {
			Entity parentEntity = entityDAO.getEntityById(entity.getParentId());
			if (parentEntity!=null) {
				list.addAll(getPermissions(parentEntity));
			}
		}
		return list;
	}
	
	/**
	 * Get permission current user has
	 * @param entity
	 * @param auth
	 * @return
	 */
	public List<ACE> getPermissions(Entity entity, String auth) {

		List<ACE> allset = getPermissions(entity);
		
		List<ACE> result = new ArrayList<ACE>();
		for (ACE ace : allset) {
			if (AuthenticationUtil.getCurrentAuthorities().contains(ace.getAuthority())) {
				result.add(ace);
			}
		}
		return result;
	}

	
	public List<ACE> getAllSetPermissions(Entity entity, boolean includeInherit) {
		List<ACE> aces = aclDAO.getACEs(entity.getId());

		if (entity.getParentId()!=null && includeInherit) {
			Entity parentEntity = entityDAO.getEntityById(entity.getParentId());
			if (parentEntity!=null) {
				aces.addAll(getAllSetPermissions(parentEntity, true));
			}
		}
		return aces;
	}

	public boolean hasPermission(Entity entity, String permission) {
		List<ACE> allset = new ArrayList<ACE>();
		
		for (ACE ace : allset) {
			if (AuthenticationUtil.getCurrentAuthorities().contains(ace.getAuthority()) && 
					ace.getPermission().equals(permission)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean hasReadPermission(Entity entity) {
		return true;
	}


	public void clearPermission(Entity entity) {
		aclDAO.removeACE(entity.getId());
	}

	public void deletePermission(Entity entity, String authority,
			String permission) {
	}

	public void setPermission(Entity entity, String authority,
			String permission, boolean allow) {
		aclDAO.addACE(entity.getId(), authority, permission, allow);
	}
	
	public void setInheritParentPermissions(Entity entity,
			boolean inheritParentPermissions) {
		
		Map<QName, Serializable> m = new HashMap<QName, Serializable>();
		m.put(Model.PROP_ACL_INHERIT, inheritParentPermissions);
		entityDAO.updateEntityProperties(entity, m);
		
		entity.setInheritAcl(inheritParentPermissions);
	}
	
	public Collection<Entity> getEntitiesBySource(String authority) {
		List<ACE> aces = aclDAO.findACEs(authority, null);
		
		Set<Entity> result = new HashSet<Entity>();
		
		for (ACE ace : aces) {
			Entity entity = entityDAO.getEntityById(ace.getEntity());
			if (entity!=null) {
				result.add(entity);
			}
		}
		return result;
	}
	
	
	public Collection<Entity> getEntitiesByTarget(String authority) {
		List<ACE> aces = aclDAO.findACEs(null, authority);
		
		Set<Entity> result = new HashSet<Entity>();
		
		for (ACE ace : aces) {
			Entity entity = entityDAO.getEntityById(ace.getEntity());
			if (entity!=null) {
				result.add(entity);
			}
		}
		return result;
	}
	
	public boolean getInheritParentPermissions(Entity entity) {
		if (entity.getInheritAcl()!=null) {
			return entity.getInheritAcl();
		} else {
			return true;
		}
	}

	
	public Map<String, List<Access>> getPermissionGroups() {
		return aclDAO.getPermissionGroups();
	}
	
}
