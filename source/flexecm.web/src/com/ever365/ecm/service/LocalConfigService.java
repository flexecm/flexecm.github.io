package com.ever365.ecm.service;

import com.ever365.ecm.authority.AuthenticationUtil;
import com.ever365.ecm.authority.PersonService;
import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.repo.Model;

/**
 * 
 * @author LiuHan
 *
 */
public class LocalConfigService {

	private PersonService personService;
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	
	
}
