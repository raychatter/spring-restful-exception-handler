package com.raychatter.common.annotation;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnnotationHandlerTest {

   @Test
   public void formatMessage_ShouldRenderUserTemplate_WhenUserTemplateGiven() throws Exception {
      final String givenUserTemplate = "A TEMPLATE: %s";
      final String exceptionMessage = "AN ERROR MESSAGE";
      final String expectedResult = "A TEMPLATE: AN ERROR MESSAGE";

      AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(givenUserTemplate).when(sut).readTemplate();

      final String actual = sut.formatMessage(new Exception(exceptionMessage));

      Assert.assertEquals(expectedResult, actual);
   }

   @Test
   public void formatDefaultMessage_ShouldRenderDefaultUserTemplate_WhenNoUserTemplateGiven() throws Exception {
      final String defaultTemplate = "DEFAULT TEMPLATE: %s";
      final String exceptionMessage = "AN ERROR MESSAGE";
      final String expectedMessage = "DEFAULT TEMPLATE: AN ERROR MESSAGE";

      AnnotationHandler spyAnnotationHandler = spy(new AnnotationHandler());
      doReturn(defaultTemplate).when(spyAnnotationHandler).readDefaultTemplate();

      final String actual = spyAnnotationHandler.formatDefaultMessage(new Exception(exceptionMessage));

      Assert.assertEquals(expectedMessage, actual);
   }

   @Test
   public void readDefaultTemplate_ShouldReturnDefaultErrorMessage_WhenIOError() throws Exception {
      final String defaultTemplatePath = "defaults/default.template";

      AnnotationHandler spyAnnotationHandler = spy(new AnnotationHandler());
      doThrow(new IOException()).when(spyAnnotationHandler).getResource(defaultTemplatePath);

      final String actualTemplate = spyAnnotationHandler.readDefaultTemplate();

      Assert.assertEquals(AnnotationHandler.DEFAULT_ERROR_STRING, actualTemplate);
   }

   @Test
   public void readTemplate_ShouldReturnUserTemplateString_WhenUserTemplateIsGiven() throws Exception {
      // arrange
      final String expectedUserTemplateString = "USER TEMPLATE";
      final InputStream expectedInputStream = new ByteArrayInputStream(expectedUserTemplateString.getBytes());

      // act
      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(expectedInputStream).when(sut).getResource(anyString());

      final String actual = sut.readTemplate();

      // assert
      Assert.assertEquals(expectedUserTemplateString, actual);
   }

   @Test
   public void readDefaultTemplate_ShouldReturnDefaultTemplateString_WhenNoUserTemplateIsGiven() throws Exception {
      // arrange
      final String expectedDefaultTemplateString = "DEFAULT TEMPLATE";
      final InputStream expectedInputStream = new ByteArrayInputStream(expectedDefaultTemplateString.getBytes());

      // act
      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(expectedInputStream).when(sut).getResource(anyString());

      final String actual = sut.readDefaultTemplate();

      // assert
      Assert.assertEquals(expectedDefaultTemplateString, actual);
   }

   @Test public void handleException_ShouldRenderDefaultContentType_WhenNoAnnotationAttributesGiven() throws Exception {
      final String emptyString = "";

      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(emptyString).when(sut).formatMessage(null);
      doReturn(emptyString).when(sut).formatDefaultMessage(null);

      sut.handleException(TestExceptionWithNoAnnotationAttributes.class.getAnnotation(ExceptionHandler.class), null, mockResponse);

      verify(mockResponse).setContentType(MediaType.TEXT_PLAIN_VALUE);
   }

   @Test public void handleException_ShouldRenderDefaultHttpStatusCode_WhenNoAnnotationAttributesGiven() throws Exception {
      final String emptyString = "";

      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(emptyString).when(sut).formatMessage(null);
      doReturn(emptyString).when(sut).formatDefaultMessage(null);

      sut.handleException(TestExceptionWithNoAnnotationAttributes.class.getAnnotation(ExceptionHandler.class), null, mockResponse);

      verify(mockResponse).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
   }

   @Test public void handleException_ShouldRenderNotFoundHttpStatusCode_WhenNotFoundAnnotationAttributeIsGiven() throws Exception {
      final String emptyString = "";

      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(emptyString).when(sut).formatMessage(null);
      doReturn(emptyString).when(sut).formatDefaultMessage(null);

      sut.handleException(TestExceptionWithNotFoundStatusCode.class.getAnnotation(ExceptionHandler.class), null, mockResponse);

      verify(mockResponse).setStatus(HttpStatus.NOT_FOUND.value());
   }

   @Test public void handleException_ShouldRenderXmlContentType_WhenXmlContentTypeAnnotationAttributeIsGiven() throws Exception {
      final String emptyString = "";

      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(emptyString).when(sut).formatMessage(null);
      doReturn(emptyString).when(sut).formatDefaultMessage(null);

      sut.handleException(TestExceptionWithXmlContentType.class.getAnnotation(ExceptionHandler.class), null, mockResponse);

      verify(mockResponse).setContentType(MediaType.APPLICATION_XML_VALUE);
   }

   @Test public void handleException_ShouldRenderUserMessage_WhenUserTemplateIsGiven() throws Exception {
      final String expectedUserTemplate = "USER TEMPLATE: %s";
      final String expectedErrorMessage = "ERROR MESSAGE";
      final String expectedErrorBody = "USER TEMPLATE: ERROR MESSAGE";
      final TestExceptionWithNoAnnotationAttributes expectedException = new TestExceptionWithNoAnnotationAttributes(expectedErrorMessage);

      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(new ByteArrayInputStream(expectedUserTemplate.getBytes())).when(sut).getResource(AnnotationHandler.USER_TEMPLATE);

      sut.handleException(expectedException.getClass().getAnnotation(ExceptionHandler.class), expectedException, mockResponse);

      verify(mockPrinter).write(expectedErrorBody);
   }

}

@ExceptionHandler()
class TestExceptionWithNoAnnotationAttributes extends Exception {
   public TestExceptionWithNoAnnotationAttributes(final String s) {
      super(s);
   }
}

@ExceptionHandler(contentType = MediaType.APPLICATION_XML_VALUE)
class TestExceptionWithXmlContentType extends Exception { }

@ExceptionHandler(httpStatus = HttpStatus.NOT_FOUND)
class TestExceptionWithNotFoundStatusCode extends Exception { }

