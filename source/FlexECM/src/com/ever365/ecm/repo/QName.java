package com.ever365.ecm.repo;


import java.io.Serializable;
import java.util.Collection;

/**
 * <code>QName</code> represents the qualified name of a Repository item. Each
 * QName consists of a local name qualified by a namespace.
 * @author Liu Han
 * 
 */
public final class QName implements Serializable, Cloneable, Comparable<QName>
{
	private static final long serialVersionUID = -6768100189820030476L;

	public static final String EMPTY_URI_SUBSTITUTE = ".empty";
    
    private final String namespaceURI;                // never null
    private final String localName;                   // never null
    private int hashCode;

    public static final String NAMESPACE_PREFIX = ":";
    public static final int MAX_LENGTH = 100;
    
    
    public QName(String namespaceURI, String localName) {
		this.namespaceURI = namespaceURI;
		this.localName = localName;
	}

	/**
     * Create a QName
     * 
     * (With no prefix)
     * 
     * @param namespaceURI  the qualifying namespace (maybe null or empty string)
     * @param localName  the local name
     * @return the QName
     */
    public static QName createQName(String namespaceURI, String localName)
    {
        if (localName == null || localName.length() == 0)
        {
            throw new IllegalArgumentException("A QName must consist of a local name");
        }
        return new QName(namespaceURI, localName);
    }
    
    public static QName createQName(Object qname) {
    	if (qname instanceof String) {
    		return createQName((String)qname);
    	} else {
    		throw new IllegalArgumentException("Argument qname is mandatory");
    	}
    }
    /**
     * Create a QName from its internal string representation of the following format:
     * 
     * <code>{namespaceURI}localName</code>
     * 
     * @param qname  the string representation of the QName
     * @return the QName
     * @throws IllegalArgumentException
     * @throws InvalidQNameException
     */
    public static QName createQName(String qname)
    {
        if (qname == null || qname.length() == 0)
        {
            throw new IllegalArgumentException("Argument qname is mandatory");
        }

        String namespaceURI = null;
        String localName = null;

        String[] names = qname.split(QName.NAMESPACE_PREFIX);
        
        if (names.length!=2) {
        	throw new IllegalArgumentException("QName '" + qname + "' must consist of a local name");
        } else {
        	namespaceURI = names[0];
        	localName = names[1];
        }
        // Construct QName
        return new QName(namespaceURI, localName);
    }

    /**
     * Create a valid local name from the specified name
     * 
     * @param name  name to create valid local name from
     * @return valid local name
     */
    public static String createValidLocalName(String name)
    {
        // Validate length
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("Local name cannot be null or empty.");
        }
        if (name.length() > MAX_LENGTH)
        {
            name = name.substring(0, MAX_LENGTH);
        }

        return name;
    }
    

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * Gets the name
     * 
     * @return the name
     */
    public String getLocalName()
    {
        return this.localName;
    }


    /**
     * Gets the namespace
     * 
     * @return the namespace (empty string when not specified, but never null)
     */
    public String getNamespaceURI()
    {
        return this.namespaceURI;
    }

    
    /**
     * Two QNames are equal only when both their name and namespace match.
     * 
     * Note: The prefix is ignored during the comparison.
     */
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object instanceof QName)
        {
            QName other = (QName)object;
            // namespaceURI and localname are not allowed to be null
            return (this.localName.equals(other.localName) &&
                    this.namespaceURI.equals(other.namespaceURI));
        }
        return false;
    }

    /**
     * Performs a direct comparison between qnames.
     * 
     * @see #equals(Object)
     */
    public boolean isMatch(QName qname)
    {
        return this.equals(qname);
    }

    /**
     * Calculate hashCode. Follows pattern used by String where hashCode is
     * cached (QName is immutable).
     */
    public int hashCode()
    {
        if (this.hashCode == 0)
        {
            // the hashcode assignment is atomic - it is only an integer
            this.hashCode = ((37 * localName.hashCode()) + namespaceURI.hashCode());
        }
        return this.hashCode;
    }


    /**
     * Render string representation of QName using format:
     * 
     * <code>{namespace}name</code>
     * 
     * @return the string representation
     */
    public String toString()
    {
        return new StringBuilder(10).append(namespaceURI).append(NAMESPACE_PREFIX).append(localName).toString();
    }

    /**
     * Uses the {@link #getNamespaceURI() namespace URI} and then the {@link #getLocalName() localname}
     * to do the comparison i.e. the comparison is alphabetical.
     */
    public int compareTo(QName qname)
    {
        int namespaceComparison = this.namespaceURI.compareTo(qname.namespaceURI);
        if (namespaceComparison != 0)
        {
            return namespaceComparison;
        }
        // Namespaces are the same.  Do comparison on localname
        return this.localName.compareTo(qname.localName);
    }

}
