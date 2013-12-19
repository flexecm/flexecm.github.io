package com.ever365.ecm.repo;

import java.util.List;

/**
 *  
 * @author Liu Han
 */
public interface QNameService {

	public static final String QNAME_PRIVATE = "i";
	public static final String QNAME_PUBLIC = "p";
	
	public QName getQName(Long id);
	
	public Long getQNameID(QName qn);

	public List<Long> getQNamesByNameSpace(String ns);
	
}
