package com.ever365.utils;

public class UUID {
	public final static String generate() {
		return java.util.UUID.randomUUID().toString();
	}
}
