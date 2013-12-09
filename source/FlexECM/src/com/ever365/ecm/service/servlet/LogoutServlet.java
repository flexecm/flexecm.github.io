package com.ever365.ecm.service.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = -8073403112922258905L;
	
	private String logoutPage;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logoutPage = config.getServletContext().getInitParameter("loginPage");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getSession().setAttribute(LoginServlet.SESSION_USER, null);
		resp.sendRedirect(logoutPage);
	}
	
}
