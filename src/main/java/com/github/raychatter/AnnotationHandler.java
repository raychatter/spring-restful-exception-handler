package com.github.raychatter;

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
   private static final String DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_XML_VALUE;
   private static final int DEFAULT_STATUS_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
   private boolean useHandledExceptionMessage = true; //use the message from the annotated exception
   private boolean useGetCause = true;

   public void setUseHandledExceptionMessage(boolean flag) {
      useHandledExceptionMessage = flag;
   }

   public void setUseGetCause(boolean flag) {
      useGetCause = flag;
   }

   @Override
   public ModelAndView resolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception thrownException) {
      final Exception handledException = getHandledException(thrownException);
      final ExceptionHandler annotation = getAnnotationFrom(handledException);
      final Exception messageException = getMessageException(thrownException, handledException);

      try {
         if (annotation == null) {
            handledException.printStackTrace();
            return respondWithDefault(messageException, response);
         }

         return handleException(annotation, messageException, response);
      } catch (IOException e) {
         // potentially something went wrong in the response itself
         e.printStackTrace();
      }

      return new ModelAndView();
   }

   protected Exception getHandledException(final Exception thrownException) {
      if(useGetCause) {
         return getAnnotatedException(thrownException);
      }
      return thrownException;
   }

   // This only matters if the user decides to use getCause
   protected Exception getMessageException(final Exception thrownException, final Exception annotatedException) {
      if(useHandledExceptionMessage) {
         return annotatedException;
      }
      return thrownException;
   }

   protected ModelAndView handleException(final ExceptionHandler annotation, final Exception handledException, final HttpServletResponse response) throws IOException {
      response.setContentType(annotation.contentType());
      response.setStatus(annotation.httpStatus().value());

      try {
         final String message = formatMessage(handledException);
         response.getWriter().write(message);
      } catch (IOException e) {
         return respondWithDefault(handledException, response);
      }

      return new ModelAndView();
   }

   protected ModelAndView respondWithDefault(final Exception handledException, final HttpServletResponse response) throws IOException {
      response.setContentType(DEFAULT_CONTENT_TYPE);
      response.setStatus(DEFAULT_STATUS_CODE);
      response.getWriter().write(formatDefaultMessage(handledException));

      return new ModelAndView();
   }

   protected Exception getAnnotatedException(Exception exception) {
      Throwable causedException = exception.getCause();
      if(getAnnotationFrom(exception) != null || causedException == null) {
         return exception;
      } else {
         return getAnnotatedException((Exception) causedException);
      }
   }

   protected ExceptionHandler getAnnotationFrom(Exception exception) {
      return exception.getClass().getAnnotation(ExceptionHandler.class);
   }

   protected String formatMessage(final Exception handledException) throws IOException {
      return String.format(readTemplate(), handledException.getMessage());
   }

   protected String formatDefaultMessage(final Exception handledException) throws IOException {
      return String.format(readDefaultTemplate(), handledException.getMessage());
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
