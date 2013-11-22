package com.ever365.rest;

public class HttpStatusException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String description;
	private HttpStatus status;
	
	public HttpStatusException(HttpStatus status) {
		super();
		this.status = status;
	}

	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	public static final int CONFLICT = 409;


	public HttpStatusException(HttpStatus hs, Exception e) {
		super();
		status = hs;
		if (e instanceof HttpStatusException) {
			this.description = ((HttpStatusException) e).getDescription();
		} else {

			StackTraceElement[] trances = e.getStackTrace();
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < trances.length; i++) {
				sb.append(trances[i].getClassName() + " " + trances[i].getMethodName() + "   " + trances[i].getLineNumber());
				sb.append("\n\r");
			}
			description = sb.toString();
		}
	}

	public int getCode() {
		return status.value();
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return null;
	}

	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}

}