package com.ever365.rest;


public class TestService {
	@RestService(uri="/hello", method="GET")
	public String Hello() {
		return "hello";
	}
}
