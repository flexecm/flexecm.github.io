package com.ever365.ecm.faceted;

import java.util.List;

public interface FacetedDAO {
	
	public void addFaceted(String parent, String title);
	
	public List<String> list();
	
	public List<String> getSubFaceteds(String parentTitle);
	
	public void removeFaceted(String title);
	
}
