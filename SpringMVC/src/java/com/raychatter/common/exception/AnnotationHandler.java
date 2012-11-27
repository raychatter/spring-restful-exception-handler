package com.raychatter.common.exception;

import com.raychatter.common.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class AnnotationHandler implements HandlerExceptionResolver {

   @Override
   public ModelAndView resolveException(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Object handler,
                                        final Exception thrownException) {

      final ExceptionHandler exceptionHandlerAnnotation = thrownException.getClass().getAnnotation(ExceptionHandler.class);
      if (exceptionHandlerAnnotation == null) {
         return new ModelAndView();
      }

      return doStuffWithAnnotation(exceptionHandlerAnnotation, thrownException, response);
   }

   private ModelAndView doStuffWithAnnotation(final ExceptionHandler exceptionHandlerAnnotation, final Exception thrownException, final HttpServletResponse response) {

      response.setContentType(exceptionHandlerAnnotation.contentType());
      response.setStatus(exceptionHandlerAnnotation.httpStatus().value());
      try {
         response.getWriter().write(formatMessage(thrownException));
      } catch (IOException e) {
         e.printStackTrace();
      }
      return new ModelAndView();
   }

//   Customize your output message here. If you have multiple dynamic parameters to
//   put into your template, you can assign them all in this method.
   private String formatMessage(final Exception thrownException) {
      return String.format(readTemplate(), thrownException.getMessage());
   }

   // Reads the template file until the end of the line
   private String readTemplate() {
      final InputStream templateFile = getClass().getResourceAsStream("/error.template");
      return new Scanner(templateFile, "UTF-8").useDelimiter("\\A").next().trim();
   }

}
