package com.raychatter.common.controller;

import com.raychatter.common.annotation.SupportedExceptions;
import com.raychatter.common.exception.CustomException;
import com.raychatter.common.exception.MyNegativeArraySizeException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@SupportedExceptions(value={CustomException.class, MyNegativeArraySizeException.class})
public class HelloController {

   @RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public void printWelcome(ModelMap model) throws Exception {

		model.addAttribute("message", "Hello, World!");
		throw new CustomException("It's broken!");
//      throw new TypeMismatchException("message",Integer.class);
//      throw new MyNegativeArraySizeException("oops");
	}
	
}