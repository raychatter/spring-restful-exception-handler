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

   //TODO: When there's a wrapper exception leave it unannotated… call the e.getCause() method… until there is an annotated exception or if there are no more causes (e.geCause()==null)??
   /* [ ] If the exception thrown is annotated, use the message from that
    * [x] If it is not annotated, call getCause() until you reach the innermost annotated exception. Use that message.
    * [ ] If getCause()==null, return the default message and response code
    */
   @Override
   public ModelAndView resolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception thrownException) {
      Exception rootException = getRootException(thrownException, thrownException.getCause());
      final ExceptionHandler annotation = getAnnotationFrom(rootException);

      try {
         if (annotation == null) {
            rootException.printStackTrace();
            return respondWithDefault(rootException, response);
         }
         return handleException(annotation, rootException, response);
      } catch (IOException e) {
         // potentially something went wrong in the response itself
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

   protected Exception getRootException(Exception thrownException, Throwable causedException) {
      if(causedException == null || getAnnotationFrom((Exception)causedException)==null) {
         return thrownException;
      } else {
         return getRootException((Exception) causedException, causedException.getCause());
      }
   }

   protected ExceptionHandler getAnnotationFrom(Exception exception) {
      return exception.getClass().getAnnotation(ExceptionHandler.class);
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
