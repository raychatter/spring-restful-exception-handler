package com.raychatter.common.exception;

import com.raychatter.common.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

@ExceptionHandler(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR, contentType = "application/json")
public class MyNegativeArraySizeException extends NegativeArraySizeException {
   public MyNegativeArraySizeException(String s) {
      super(s);
   }
}
