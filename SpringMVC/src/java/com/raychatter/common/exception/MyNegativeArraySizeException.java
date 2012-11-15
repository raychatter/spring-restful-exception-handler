package com.raychatter.common.exception;

import com.raychatter.common.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

@ExceptionHandler(httpStatus = HttpStatus.BAD_REQUEST,contentType = "application/JSON", message = "{\"errors\": \"%s\" }")
public class MyNegativeArraySizeException extends NegativeArraySizeException {
   public MyNegativeArraySizeException(String s) {
      super(s);
   }
}
