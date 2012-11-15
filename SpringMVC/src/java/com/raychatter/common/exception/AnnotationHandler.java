package com.raychatter.common.exception;

import com.raychatter.common.annotation.ExceptionHandler;
import com.raychatter.common.annotation.SupportedExceptions;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

      final SupportedExceptions supportedExceptionsAnnotation = handler.getClass().getAnnotation(SupportedExceptions.class);
      if (supportedExceptionsAnnotation == null) {
         // TODO: test what you get in UI when you return here, also test what happens if you return 'null';
         return new ModelAndView();
      }

      final Set<Class<? extends Throwable>> exceptions = new HashSet<Class<? extends Throwable>>();
      Collections.addAll(exceptions, supportedExceptionsAnnotation.value());

      if(exceptions.contains(thrownException.getClass())) {
         return doStuffWithAnnotation(thrownException,response);
      }

      return doStuffWithAnnotation(thrownException,response);
   }

   private ModelAndView doStuffWithAnnotation(final Exception thrownException, final HttpServletResponse response) {
      final ExceptionHandler exceptionHandlerAnnotation = thrownException.getClass().getAnnotation(ExceptionHandler.class);
      if (exceptionHandlerAnnotation == null) {
         // TODO: test what you get in UI when you return here, also test what happens if you return 'null';
         return new ModelAndView();
      }
      response.setStatus(exceptionHandlerAnnotation.httpStatus().value());
      response.setContentType(exceptionHandlerAnnotation.contentType());
      try {
         response.getWriter().write(String.format(exceptionHandlerAnnotation.message(), thrownException.getMessage()));
      } catch (IOException e) {
         e.printStackTrace();
      }
      return new ModelAndView();
   }

}
