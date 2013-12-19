package com.ever365.rest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface RestParam {
	String value() default "";
	boolean required() default false;
}
