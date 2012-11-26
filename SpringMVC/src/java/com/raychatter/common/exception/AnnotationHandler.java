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

//      Class<?> handlerClass = handler.getClass();
//      if (handler instanceof org.springframework.web.method.HandlerMethod) {
//         final HandlerMethod router = (HandlerMethod) handler;
//         handlerClass = router.getBeanType();
//      }

      final ExceptionHandler exceptionHandlerAnnotation = thrownException.getClass().getAnnotation(ExceptionHandler.class);
      if (exceptionHandlerAnnotation == null) {
         // TODO: test what you get in UI when you return here, also test what happens if you return 'null';
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

   private String formatMessage(final Exception thrownException) {
      return String.format(readTemplate(), thrownException.getMessage());
   }

   private String readTemplate() {
      final InputStream templateFile = getClass().getResourceAsStream("/error.template");
      return new Scanner(templateFile, "UTF-8").useDelimiter("\\A").next().trim();
   }

}
