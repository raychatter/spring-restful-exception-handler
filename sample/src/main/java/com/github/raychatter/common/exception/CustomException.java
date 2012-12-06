package com.github.raychatter.common.exception;

import com.github.raychatter.ExceptionHandler;
import org.springframework.http.HttpStatus;

@ExceptionHandler(httpStatus = HttpStatus.NOT_FOUND, contentType = "text/html")
public class CustomException extends Exception {

   public CustomException(final String s) {
      super(s);
   }

}
