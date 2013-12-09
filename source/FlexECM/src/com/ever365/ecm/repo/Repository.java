package com.ever365.ecm.repo;

import java.io.Serializable;

import com.ever365.ecm.entity.Entity;

/**
 * Repository which has protocol and identifier
 * The Protocol means how the repo would be used 
 * @author Liu Han
 */
public final class Repository implements Serializable
{
    private static final long serialVersionUID = 3905808565129394486L;
    
    
    public static final String URI_FILLER = "://";
    public static final String PROTOCOL_USR = "usr";
    public static final String PROTOCOL_PUB = "pub";
    public static final String REPOSITORY_DEFAULT = "pub://default";
    public static final String REPOSITORY_PERSON = "person://all";
    

    private String id;
    private final String protocol;
    private final String identifier;
    private String owner;
    private Entity rootEntity;
    private Entity trashEntity;
    private String storeName;
    
    public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getOwner() {
		return owner;
	}
	public Entity getRootEntity() {
		return rootEntity;
	}
	public void setRootEntity(Entity rootEntity) {
		this.rootEntity = rootEntity;
	}
	
	public Entity getTrashEntity() {
		return trashEntity;
	}
	public void setTrashEntity(Entity trashEntity) {
		this.trashEntity = trashEntity;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
     * @param protocol
     *            well-known protocol for the store
     * @param identifier
     *            the identifier, which may be specific to the protocol
     */
    public Repository(String protocol, String identifier)
    {
        if (protocol == null)
        {
            throw new IllegalArgumentException("Repository protocol may not be null");
        }
        if (identifier == null)
        {
            throw new IllegalArgumentException("Repository identifier may not be null");
        }

        this.protocol = protocol;
        this.identifier = identifier;
    }
    public Repository(String name)
    {
    	String[] arrays = name.split(URI_FILLER);
    	if (arrays.length!=2) {
    		throw new IllegalArgumentException("Repository protocol may not be null");
    	} else {
    		this.protocol = arrays[0];
    		this.identifier = arrays[1];
    	}
    }

    public String toString()
    {
        return protocol + URI_FILLER + identifier;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof Repository)
        {
            Repository that = (Repository) obj;
            return (this.protocol.equals(that.protocol)
                    && this.identifier.equals(that.identifier));
        } else
        {
            return false;
        }
    }
    
    /**
     * Creates a hashcode from both the {@link #getProtocol()} and {@link #getIdentifier()}
     */
    public int hashCode()
    {
        return (protocol.hashCode() + identifier.hashCode());
    }

    public String getProtocol()
    {
        return protocol;
    }

    public String getIdentifier()
    {
        return identifier;
    }
}