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

   protected static final String DEFAULT_ERROR_STRING = "<error>%s</error>";
   protected static final String USER_TEMPLATE = "error.template";
   protected static final String DEFAULT_TEMPLATE = "defaults/default.template";
   private static final String UTF_8 = "UTF-8";

   @Override
   public ModelAndView resolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception thrownException) {
      final ExceptionHandler annotation = getAnnotationFrom(thrownException);

      try {
         if (annotation == null) {
            thrownException.printStackTrace();
            return respondWithDefault(thrownException, response);
         }

         return handleException(annotation, thrownException, response);
      } catch (IOException e) {
         // potentially something went wrong in response itself
         e.printStackTrace();
      }

      return new ModelAndView();
   }

   protected ModelAndView handleException(final ExceptionHandler annotation, final Exception thrownException, final HttpServletResponse response) throws IOException {
      response.setContentType(annotation.contentType());
      response.setStatus(annotation.httpStatus().value());

      try {
         final String message = formatMessage(thrownException);
         response.getWriter().write(message);
      } catch (IOException e) {
         return respondWithDefault(thrownException, response);
      }

      return new ModelAndView();
   }

   protected ModelAndView respondWithDefault(final Exception thrownException, final HttpServletResponse response) throws IOException {
      response.setContentType(MediaType.APPLICATION_XML_VALUE);
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.getWriter().write(formatDefaultMessage(thrownException));

      return new ModelAndView();
   }

   protected ExceptionHandler getAnnotationFrom(Exception thrownException) {
      return thrownException.getClass().getAnnotation(ExceptionHandler.class);
   }

   protected String formatMessage(final Exception thrownException) throws IOException {
      return String.format(readTemplate(), thrownException.getMessage());
   }

   protected String formatDefaultMessage(final Exception thrownException) throws IOException {
      return String.format(readDefaultTemplate(), thrownException.getMessage());
   }

   protected String readTemplate() throws IOException {
      final InputStream templateFile = getResource(USER_TEMPLATE);
      return new Scanner(templateFile, UTF_8).useDelimiter("\\A").next().trim();
   }

   protected String readDefaultTemplate() {
      try {
         final InputStream templateFile = getResource(DEFAULT_TEMPLATE);
         return new Scanner(templateFile, UTF_8).useDelimiter("\\A").next().trim();
      } catch (IOException ex) {
         return DEFAULT_ERROR_STRING;
      }
   }

   protected InputStream getResource(final String resource) throws IOException {
      return new ClassPathResource(resource).getInputStream();
   }

}
