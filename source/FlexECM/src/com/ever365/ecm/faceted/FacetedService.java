package com.ever365.ecm.faceted;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.entity.EntityDAO;
import com.ever365.ecm.repo.Model;
import com.ever365.ecm.repo.QName;
import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;
/**
 * @author LiuHan
 */
public class FacetedService {
	
	private FacetedDAO facetedDAO;
	private EntityDAO entityDAO;

	public void setFacetedDAO(FacetedDAO facetedDAO) {
		this.facetedDAO = facetedDAO;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@RestService(uri="/faceted/set", method="POST")
	public void setEntityFaceted(@RestParam(value="id") List<String> id, @RestParam(value="list") List<String> list) {
		for (String string : id) {
			Entity entity = entityDAO.getEntityById(string);
			if (entity==null) throw new HttpStatusException(HttpStatus.BAD_REQUEST);
			
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(Model.FACETED, (Serializable) list);
			
			entityDAO.updateEntityProperties(entity, properties);
		}
	}

	@RestService(uri="/faceted/add", method="POST")
	public void addFaceted(@RestParam(value="parent") String parent, @RestParam(value="title") String name) {
		facetedDAO.addFaceted(parent, name);
	}
	
	
	@RestService(uri="/faceted/filter", method="GET")
	public List<Map<String, Object>> filterFileByFaceted(@RestParam(value="exts") String ext, @RestParam(value="sf") Long sizeFrom, 
			 @RestParam(value="st") Long sizeTo, @RestParam(value="creator") String creator, @RestParam(value="faceted") List<String> faceted) {
		
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("type", Model.TYPE_FILE.toString());
		
		entityDAO.filter(filter);
		return null;
	}
	
	
	
	@RestService(uri="/faceted/list", method="GET")
	public List<String> listFaceted() {
		return facetedDAO.list();
	}
	
	
	public void setEntityFaceted(String entity, ArrayList<String> faceted) {
		Map<QName, Serializable>  updates = new HashMap<QName, Serializable>();
		updates.put(Model.FACETED, faceted);
		entityDAO.updateEntityProperties(entityDAO.getEntityById(entity), updates);
	}
	
}
