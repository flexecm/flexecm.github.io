/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package com.ever365.ecm.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ever365.ecm.repo.QName;
import com.ever365.ecm.repo.Repository;

/**
 * DAO services for <b>entity</b> and related 
 * @author Liu Han
 */
public interface EntityDAO 
{
    public boolean exists(Entity entity);
    
    public String getNodeType(Long nodeId);
    
    public Long getNodeAclId(Long nodeId);
    
    public List<Entity> listChild(Entity entity, int offset, int count);
    
    public List<Entity> listChildByType(String parentId, String type, int offset, int count);
    
    public Long getChildrenCount(String parentId);
    
    public Entity getEntityByPath(Entity entity, String path);
    
    public Entity getChildByName(Entity entity, String childName);
    
    public String getEntityPath(Entity entity);

    public Entity getEntityById(String Id);
    
    public List<String> getDescendants(String Id);

    public void move(Entity src,  Entity target, Map<QName, Serializable> props);
    
    public void copy(Entity src, Entity target, String newName);
    
    /**
     * get ancestors in sequence
     * @param repository
     * @param Id
     * @return List of ancestor ids;
     */
    public LinkedList<Entity> getAncestor(Entity entity);
    
    /**
     * Create a new entity.  Note that allowing the <b>uuid</b> to be assigned by passing in a <tt>null</tt>
     * is more efficient.
     * @param repository
     * @param parentNodeId
     * @param assocType
     * @param assocName
     * @param uuid
     * @param nodeType
     * @param childNodeName
     * @param auditableProperties
     * @return
     */
    public Entity addEntity(
    		Repository repository,
            String parentNodeId,
            QName assocType,
            String uuid,
            QName nodeType,
            String childNodeName,
            Map<QName, Serializable> auditableProperties,
            Map<QName, Serializable> specialProperties);
    
    public void updateEntityProperties(Entity entity, 
            Map<QName, Serializable> specialProperties);
    
  
    public void setNodeAclId(Long nodeId, Long aclId);
    
    public void setPrimaryChildrenSharedAclId(
            Long primaryParentNodeId,
            Long optionalOldSharedAlcIdInAdditionToNull,
            Long newSharedAclId);

    public void deleteEntity(Entity entity);
    
    public Map<String, Serializable> getEntityProperties(String id);
    
    public boolean addNodeProperty(Long nodeId, String qname, Serializable value);
    
    public boolean addNodeProperties(Long nodeId, Map<String, Serializable> properties);
    
    public boolean removeNodeProperties(Long nodeId, Set<String> propertyQNames);
    
    public Set<String> getEntityAspects(String nodeId);
    
    public boolean hasAspect(String nodeId, String aspectQName);
    
    public boolean addAspects(String nodeId, Set<String> aspectQNames);
    
    public boolean removeNodeAspects(String nodeId);
    
    public boolean removeNodeAspects(String nodeId, Set<String> aspectQNames);

    
    /**
     * Remove a specific node association
     * 
     * @param assocId           the node association ID to remove
     * @return                  Returns the number of associations removed
     */
    public int removeNodeAssoc(Long sourceNodeId, Long targetNodeId, String assocTypeQName);
    
    /**
     * Remove all node associations that share the given node.
     * 
     * @param nodeId            the source or target of the associations
     * @return                  Returns the number of associations removed
     */
    public int removeNodeAssocsToAndFrom(Long nodeId);
    
    
    public List<Entity> filter(Map<String, Object> filters);
   
}
