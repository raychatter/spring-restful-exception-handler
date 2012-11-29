package com.raychatter.common.annotation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class AnnotationHandler implements HandlerExceptionResolver {

   @Override
   public ModelAndView resolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception thrownException) {
      final ExceptionHandler annotation = thrownException.getClass().getAnnotation(ExceptionHandler.class);

//    This still returns an empty ModelAndView
      if (annotation == null) {
         return new ModelAndView();
      }

      return doStuffWithAnnotation(annotation, thrownException, response);
   }

   private ModelAndView doStuffWithAnnotation(final ExceptionHandler exceptionHandlerAnnotation, final Exception thrownException, final HttpServletResponse response) {

//    This is only outside of the try because the null annotation case is handled in resolveException
      response.setContentType(exceptionHandlerAnnotation.contentType());
      response.setStatus(exceptionHandlerAnnotation.httpStatus().value());

      try {
         response.getWriter().write(formatMessage(thrownException));
      } catch (IOException e) {
         response.setContentType(MediaType.APPLICATION_XML_VALUE);
         response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

         try {
            response.getWriter().write(formatDefaultMessage(thrownException));
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }

      return new ModelAndView();
   }

   protected String formatMessage(final Exception thrownException) throws IOException {
      return String.format(readTemplate(), thrownException.getMessage());
   }

   protected String formatDefaultMessage(final Exception thrownException) throws IOException {
      return String.format(readDefaultTemplate(), thrownException.getMessage());
   }

   protected String readTemplate() throws IOException {
      final InputStream templateFile = new ClassPathResource("error.template").getInputStream();
      return new Scanner(templateFile, "UTF-8").useDelimiter("\\A").next().trim();
   }

// Extract into a new protected method to grab input stream; spy that new method, returning null?
   protected String readDefaultTemplate() {
      try {
         final InputStream templateFile = getInputStream("defaults/default.template");
         return new Scanner(templateFile, "UTF-8").useDelimiter("\\A").next().trim();
      } catch (IOException ex) {
         return "Error: %s";
      }
   }

   protected InputStream getInputStream(String filepath) throws IOException {
      return new ClassPathResource(filepath).getInputStream();
   }

}
