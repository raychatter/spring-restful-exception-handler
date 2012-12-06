package com.github.raychatter.common.controller;

import com.github.raychatter.common.exception.MyNegativeArraySizeException;
import com.github.raychatter.common.exception.CustomException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HelloController {

   @RequestMapping(value = "/welcome", method = RequestMethod.GET)
   @ResponseBody
   public Object welcome() {
      return "Hello World";
   }

   @RequestMapping(value = "/custom404", method = RequestMethod.GET)
   @ResponseBody
   public Object custom404() throws Exception {
      throw new CustomException("It's broken!");
   }

   @RequestMapping(value = "/custom500", method = RequestMethod.GET)
   @ResponseBody
	public Object custom500() throws Exception {
      throw new MyNegativeArraySizeException("oops");
	}

}
