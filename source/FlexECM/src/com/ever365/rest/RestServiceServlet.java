package com.ever365.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.ever365.ecm.authority.AuthenticationUtil;
import com.ever365.ecm.content.ContentData;

/**
 * Servlet implementation class RestServiceServlet
 */
public class RestServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private HttpServiceRegistry registry;
	Logger logger = Logger. getLogger(RestServiceServlet.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RestServiceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	public void init(ServletConfig config) throws ServletException {
		/*
		ApplicationContext context =
			    new ClassPathXmlApplicationContext(new String[] {"ecm-service-context.xml"});
		*/
		registry = (HttpServiceRegistry)ContextLoaderListener.getCurrentWebApplicationContext().getBean("http.registry");
		logger.info(registry.toString());
		//registry.setInjectedServices(injectedServices);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strPath = URLDecoder.decode( request.getRequestURI(), "UTF-8");
		String servletPath = request.getServletPath();
		
		int rootPos = strPath.indexOf(servletPath);
		if ( rootPos != -1)
			strPath = strPath.substring( rootPos + servletPath.length());
		
		MethodInvocation handler = registry.getGet(strPath);
		
		if (handler==null) {
			response.setStatus(404);
			return;
		}
		
		if (handler.isRunAsAdmin() && !AuthenticationUtil.isAdmin()) {
			throw new HttpStatusException(HttpStatus.FORBIDDEN);
		}
		
		Enumeration paramNames = request.getParameterNames();
		Map<String, Object> args = new HashMap<String, Object>();
		while (paramNames.hasMoreElements()) {
			String name = (String) paramNames.nextElement();
			args.put(name, URLDecoder.decode(request.getParameter(name), "UTF-8"));
		}
		
		try {
			Object result = handler.execute(args);
			render(request, response, result);
		} catch (Exception e) {
			if (e instanceof HttpStatusException) {
				response.sendError(((HttpStatusException)e).getCode(), ((HttpStatusException)e).getDescription());
			} else {
				try {
					Object result = handler.execute(args);
					render(request, response, result);
				} catch (Exception ex) {
					e.printStackTrace(response.getWriter());
					response.setStatus(500);
				}
			}
		} finally {
			doCleanUp();
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String strPath = URLDecoder.decode( request.getRequestURI(), "UTF-8");
			//request.setCharacterEncoding("UTF-8"); 
			String servletPath = request.getServletPath();

			int rootPos = strPath.indexOf(servletPath);
			if ( rootPos != -1)
				strPath = strPath.substring( rootPos + servletPath.length());

			MethodInvocation handler = registry.getPost(strPath);
			
			if (handler==null) {
				response.setStatus(404);
				return;
			}
			
			if (handler.isRunAsAdmin() && !AuthenticationUtil.isAdmin()) {
				throw new HttpStatusException(HttpStatus.FORBIDDEN);
			}

			Map<String, Object> args = new HashMap<String, Object>();
			
			if (handler.isMultipart() && ServletFileUpload.isMultipartContent(request)) {
				//这是post的上传流请求
				DiskFileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				// Parse the request
				List<FileItem> items = upload.parseRequest(request);

				args = new HashMap<String, Object>();
				//boolean hasFile = false;
				for (FileItem item : items) {
					
					if (item.isFormField()) {
						args.put(item.getFieldName(), item.getString("UTF-8"));
					} else {
						args.put(item.getFieldName(), item.getInputStream());
						args.put("size", item.getSize());
					}
					
				}
			} else {
				Enumeration paramNames = request.getParameterNames();
				while (paramNames.hasMoreElements()) {
					String name = (String) paramNames.nextElement();
					if (name.endsWith("[]")) {
						String pureName = name.substring(0, name.length()-2);
						String[] values = request.getParameterValues(name);
						
						List<String> list = new ArrayList<String>();
						for (int i = 0; i < values.length; i++) {
							list.add(URLDecoder.decode(values[i], "UTF-8"));
						}
						args.put(pureName, list);
					} else if (name.endsWith("]") && name.indexOf("[")>-1) {
						String pureName = name.substring(0, name.indexOf("["));
						if (args.get(pureName)==null) {
							args.put(pureName, new HashMap<String, String>());
						}
						((Map)args.get(pureName)).put(name.substring(name.indexOf("[")+1, name.indexOf("]")), URLDecoder.decode(request.getParameter(name), "UTF-8"));
					} else {
						args.put(name, URLDecoder.decode(request.getParameter(name), "UTF-8"));
					}
				}
			}
			
 			Object result = handler.execute(args);
			if (result==null) {
				response.setStatus(200);
			} else {
				render(request, response, result);
			}
		} catch (Exception e) {
			if (e instanceof HttpStatusException) {
				response.getWriter().println(extractError(e));
				response.sendError(((HttpStatusException)e).getCode(), ((HttpStatusException)e).getDescription());
			} else {
				e.printStackTrace();
				response.sendError(500);
				response.getWriter().println(extractError(e));
			}
		} finally {
			doCleanUp();
		}
	}

	public void doCleanUp() {

	}

	public String extractError(Exception e) {
		StackTraceElement[] trances = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < trances.length; i++) {
			sb.append(trances[i].getClassName() + "  " + trances[i].getLineNumber() + "\n");
		}
		sb.append(e.getMessage());
		return sb.toString();
	}

	private void render(HttpServletRequest request, HttpServletResponse response, Object result)
			throws IOException {
		if (result==null) {
			response.setStatus(201);
			return;
		}
		
		if (result instanceof ContentData) {
			handleFileDownload(request, response, result);
		} else {
			response.setContentType(CONTENT_TYPE);
			PrintWriter pw = response.getWriter();
			if (result instanceof Collection) {
				JSONArray ja = new JSONArray((Collection) result);
				pw.print(ja.toString());
			} else if (result instanceof Map){
				JSONObject jo = new JSONObject((Map) result);
				pw.print(jo.toString());
			} else {
				pw.print(result.toString());
			}
			pw.close();
		}
		
	}

	public void handleFileDownload(HttpServletRequest request,
			HttpServletResponse response, Object result)
			throws UnsupportedEncodingException, IOException {
		ContentData contentData = (ContentData)result; 
		
		long modifiedSince = request.getDateHeader("If-Modified-Since");
		if (modifiedSince>0L) {
			if (contentData.getLastModified()<=modifiedSince) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
		} else {
			response.setDateHeader("Last-Modified", contentData.getLastModified());
			response.setHeader("Cache-Control", "must-revalidate");
			response.setHeader("ETag", "\"" + contentData.getMd5() + "\"");
		}
		
		String fileDwnName = new String(contentData.getFileName().getBytes("UTF-8"), "ISO-8859-1");

		String userAgent = request.getHeader("User-Agent");
		if (userAgent!=null && userAgent.contains("MSIE")) {
			fileDwnName = new String(fileDwnName.getBytes("gb2312"), "ISO8859-1");
		}
		
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileDwnName + "\";");
		
		response.setHeader("Accept-Ranges", "bytes");
		
		//断点下载处理
		String range = request.getHeader("Content-Range");
		if (range == null) {
			range = request.getHeader("Range");
		}
		if (range != null)  {
			
		}
		
		response.setContentType(contentData.getMimeType());
		int size = contentData.getLength();
		response.setHeader("Content-Range", "bytes 0-"
				+ Long.toString(size - 1L) + "/"
				+ Long.toString(size));
		response.setContentLength(size);
		response.setHeader("Content-Length", Integer.toString(size));
		
		FileCopyUtils.copy(contentData.getInputStream(), response.getOutputStream());
	}

}