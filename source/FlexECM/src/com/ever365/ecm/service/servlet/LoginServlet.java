package com.ever365.ecm.service.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoaderListener;

import com.ever365.ecm.authority.PersonService;

public class LoginServlet extends HttpServlet {

	public static final String SESSION_USER = ".user";

	private static final long serialVersionUID = -8073403112922258905L;

	private PersonService personService;
	
	private String logonPage;
	private String loginPage;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		personService = (PersonService)ContextLoaderListener.getCurrentWebApplicationContext().getBean("rest.person");
		
		logonPage = config.getServletContext().getInitParameter("logonPage");
		loginPage = config.getServletContext().getInitParameter("loginPage");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String name = req.getParameter("name");
		String password = req.getParameter("password");
				
		if (name==null || password==null) {
			resp.sendRedirect("/login.html?用户名和密码不能为空");
			return;
		}

		boolean checked = personService.checkPassword(name, password);
		
		if (checked) {
			req.getSession().setAttribute(SESSION_USER, name);
			resp.sendRedirect(logonPage);
		} else {
			resp.sendRedirect(loginPage + "?user and password mismatch");
		}
	}

	
	
}
