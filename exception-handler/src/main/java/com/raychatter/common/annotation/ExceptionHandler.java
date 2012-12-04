package com.raychatter.common.annotation;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExceptionHandler {
   HttpStatus httpStatus() default HttpStatus.INTERNAL_SERVER_ERROR;
   String contentType() default MediaType.APPLICATION_XML_VALUE;
}
