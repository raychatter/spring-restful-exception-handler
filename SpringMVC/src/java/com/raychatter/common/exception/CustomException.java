package com.raychatter.common.exception;

// We like individual annotations better

import com.raychatter.common.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

@ExceptionHandler(httpStatus = HttpStatus.NOT_FOUND,contentType = "text/html")
public class CustomException extends Exception {

   public static final String ERROR_MESSAGE = "a lot of good things";

   public CustomException(final String s) {
      super(s);
   }

}
