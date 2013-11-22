package com.ever365.ecm.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ever365.ecm.repo.Model;
import com.ever365.ecm.repo.QName;
import com.ever365.ecm.repo.Repository;

/**
 * Reference to a Entity
 * 
 * @author Liu Han
 */
public final class Entity implements Serializable
{
	private static final long serialVersionUID = -754041141976823274L;
	private static final String URI_FILLER = "/";
    private static final Pattern nodeRefPattern = Pattern.compile(".+://.+/.+");
    
    private final Repository repository;
    private final String id;
    
    private QName type;
    private String name;
    
    private String creator;
    private String modifier;
    private Long created;
    private Long modified;
    private String owner;
    
    private QName assocationType;
    private QName assocationName;
    
    private String parentId;
    private String uuid;
    
    private List<QName> aspects;
    private Boolean acl;
    private Boolean inheritAcl;
    
    private Long size; 
    private Long seq;
    
    public Boolean getInheritAcl() {
		return inheritAcl;
	}

	public void setInheritAcl(Boolean inheritAcl) {
		this.inheritAcl = inheritAcl;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
	
	public Object getProperty(QName key) {
		if (key.getNamespaceURI().equals(Model.SYSTEM_NAMESPACE)) {
			return rawMap.get(key.getLocalName());
		} else {
			return rawMap.get(key.toString());
		}
	}

	public String getPropertyStr(QName key) {
		Object o = getProperty(key);
		if (o==null) return null;
		return o.toString();
	}
	
	private Map<String, Object> rawMap;

	public void setRawMap(Map<String, Object> rawMap) {
		this.rawMap = rawMap;
	}

	public QName getType() {
		return type;
	}

	public void setType(QName type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getModified() {
		return modified;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public QName getAssocationType() {
		return assocationType;
	}

	public void setAssocationType(QName assocationType) {
		this.assocationType = assocationType;
	}

	public QName getAssocationName() {
		return assocationName;
	}

	public void setAssocationName(QName assocationName) {
		this.assocationName = assocationName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public List<QName> getAspects() {
		return aspects;
	}

	public void setAspects(List<QName> aspects) {
		this.aspects = aspects;
	}

	public Boolean getAcl() {
		return acl;
	}

	public void setAcl(Boolean acl) {
		this.acl = acl;
	}

	/**
     * @see #NodeRef(StoreRef, String)
     * @see StoreRef#StoreRef(String, String)
     */
    public Entity(String protocol, String identifier, String id)
    {
        this(new Repository(protocol, identifier), id);
    }
    
    /**
     * Construct a Node Reference from a Store Reference and Node Id
     * 
     * @param repository store reference
     * @param id the manually assigned identifier of the node
     */
    public Entity(Repository repository, String id)
    {
        if (repository == null)
        {
            throw new IllegalArgumentException("Store reference may not be null");
        }
        if (id == null)
        {
            throw new IllegalArgumentException("Node id may not be null");
        }

        this.repository = repository;
        this.id = id;
    }

    @Override
    public String toString()
    {
        return repository.toString() + URI_FILLER + id;
    }

    /**
     * Override equals for this ref type
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof Entity)
        {
            Entity that = (Entity) obj;
            return (this.id.equals(that.id));
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Hashes on ID alone.  As the number of copies of a particular node will be minimal, this is acceptable
     */
    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    /**
     * @return The StoreRef part of this reference
     */
    public final Repository getRepository()
    {
        return repository;
    }

    /**
     * @return The Node Id part of this reference
     */
    public final String getId()
    {
        return id;
    }

   
    /***
     * rendered output for json 
     * @return
     */
    public Map<String, Object> toMap() {
    	return rawMap;
    }
    
}