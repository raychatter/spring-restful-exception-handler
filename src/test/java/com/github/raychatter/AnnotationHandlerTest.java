package com.github.raychatter;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static junit.framework.Assert.assertEquals;
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

   @Test
   public void handleException_ShouldRenderDefaultContentType_WhenNoAnnotationAttributesGiven() throws Exception {
      final String emptyString = "";

      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(emptyString).when(sut).formatMessage(null);
      doReturn(emptyString).when(sut).formatDefaultMessage(null);

      sut.handleException(TestExceptionWithNoAnnotationAttributes.class.getAnnotation(ExceptionHandler.class), null, mockResponse);

      verify(mockResponse).setContentType(MediaType.APPLICATION_XML_VALUE);
   }

   @Test
   public void handleException_ShouldRenderDefaultHttpStatusCode_WhenNoAnnotationAttributesGiven() throws Exception {
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

   @Test
   public void handleException_ShouldRenderNotFoundHttpStatusCode_WhenNotFoundAnnotationAttributeIsGiven() throws Exception {
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

   @Test
   public void handleException_ShouldRenderXmlContentType_WhenXmlContentTypeAnnotationAttributeIsGiven() throws Exception {
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

   @Test
   public void handleException_ShouldRenderUserMessage_WhenUserTemplateIsGiven() throws Exception {
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

   @Test
   public void handleException_ShouldReturnXmlContentType_WhenNoUserTemplateGiven() throws Exception {
      final String emptyString = "";
      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doThrow(IOException.class).when(sut).getResource(AnnotationHandler.USER_TEMPLATE);
      doReturn(emptyString).when(sut).formatDefaultMessage(null);

      sut.handleException(TestExceptionWithNoContentStatusCodeAndTextContentType.class.getAnnotation(ExceptionHandler.class), null, mockResponse);

      verify(mockResponse).setContentType(MediaType.APPLICATION_XML_VALUE);
   }

   @Test
   public void handleException_ShouldReturnInternalServerErrorStatusCode_WhenNoUserTemplateGiven() throws Exception {
      final String emptyString = "";
      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doThrow(IOException.class).when(sut).getResource(AnnotationHandler.USER_TEMPLATE);
      doReturn(emptyString).when(sut).formatDefaultMessage(null);

      sut.handleException(TestExceptionWithNoContentStatusCodeAndTextContentType.class.getAnnotation(ExceptionHandler.class), null, mockResponse);

      verify(mockResponse).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
   }

   @Test
   public void handleException_ShouldRenderDefaultTemplate_WhenNoUserTemplateGiven() throws Exception {
      final String expectedDefaultTemplate = "DEFAULT TEMPLATE: %s";
      final String expectedErrorMessage = "ERROR MESSAGE";
      final String expectedErrorBody = "DEFAULT TEMPLATE: ERROR MESSAGE";
      final TestExceptionWithNoContentStatusCodeAndTextContentType expectedException = new TestExceptionWithNoContentStatusCodeAndTextContentType(expectedErrorMessage);
      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doThrow(IOException.class).when(sut).getResource(AnnotationHandler.USER_TEMPLATE);
      doReturn(new ByteArrayInputStream(expectedDefaultTemplate.getBytes())).when(sut).getResource(AnnotationHandler.DEFAULT_TEMPLATE);

      sut.handleException(expectedException.getClass().getAnnotation(ExceptionHandler.class), expectedException, mockResponse);

      verify(mockPrinter).write(expectedErrorBody);
   }

   @Test public void resolveException_ShouldReturnCustomErrorMessage_WhenValidExceptionWithAnnotationIsGiven() throws Exception {
      final ExceptionHandler mockAnnotation = mock(ExceptionHandler.class);
      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final TestExceptionWithNoAnnotationAttributes expectedException = new TestExceptionWithNoAnnotationAttributes("");

      final AnnotationHandler sut = spy(new AnnotationHandler());
      doReturn(new ModelAndView()).when(sut).handleException(mockAnnotation, expectedException, mockResponse);
      doReturn(mockAnnotation).when(sut).getAnnotationFrom(expectedException);

      final ModelAndView view = sut.resolveException(null, mockResponse, null, expectedException);

      verify(sut).handleException(mockAnnotation, expectedException, mockResponse);
   }

   @Test public void getMessageException_ShouldReturnHandledException_WhenUseMessageExceptionIsTrue() throws Exception {
      final TestExceptionWithNoAnnotationAttributes expectedException = new TestExceptionWithNoAnnotationAttributes("");
      final TestExceptionWithNotFoundStatusCode thrownException = new TestExceptionWithNotFoundStatusCode();

      final AnnotationHandler sut = spy(new AnnotationHandler());
      sut.setUseHandledExceptionMessage(true);

      Exception messageException = sut.getMessageException(thrownException, expectedException);

      assertEquals(expectedException, messageException);
   }

   @Test public void getMessageException_ShouldReturnThrownException_WhenUseMessageExceptionIsFalse() throws Exception {
      final TestExceptionWithXmlContentType annotatedException = new TestExceptionWithXmlContentType();
      final TestExceptionWithNoAnnotation expectedException = new TestExceptionWithNoAnnotation("");

      final AnnotationHandler sut = spy(new AnnotationHandler());
      sut.setUseHandledExceptionMessage(false);

      Exception messageException = sut.getMessageException(expectedException, annotatedException);

      assertEquals(expectedException, messageException);
   }

   @Test public void resolveException_ShouldReturnDefaultErrorMessage_WhenUncheckedExceptionIsGiven() throws Exception {
      final NullPointerException expectedException = mock(NullPointerException.class);

      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
      final PrintWriter mockPrinter = mock(PrintWriter.class);
      when(mockResponse.getWriter()).thenReturn(mockPrinter);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      when(sut.getAnnotatedException(expectedException)).thenReturn(expectedException);
      when(sut.getAnnotationFrom(expectedException)).thenReturn(null);

      final ModelAndView view = sut.resolveException(null, mockResponse, null, expectedException);

      verify(sut).respondWithDefault(expectedException, mockResponse);
   }

   @Test public void getAnnotatedException_ShouldReturnOutermostAnnotatedException_WhenThereAreCheckedExceptionsChainedAndGetCauseIsTrue() throws Exception {
      TestExceptionWithNoAnnotationAttributes mockException = mock(TestExceptionWithNoAnnotationAttributes.class);
      doReturn(new TestExceptionWithXmlContentType()).when(mockException).getCause();

      final ExceptionHandler mockAnnotation = mock(ExceptionHandler.class);
      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      when(sut.getAnnotationFrom(mockException)).thenReturn(mockAnnotation);
      doReturn(new ModelAndView()).when(sut).handleException(mockAnnotation, mockException, mockResponse);

      final ModelAndView view = sut.resolveException(null, mockResponse, null, mockException);

      Assert.assertTrue(sut.getAnnotatedException(mockException) instanceof TestExceptionWithNoAnnotationAttributes);
   }

   @Test public void getHandledException_ShouldReturnThrownException_WhenThereAreCheckedExceptionsChainedAndGetCauseIsFalse() throws Exception {
      TestExceptionWithNoAnnotationAttributes mockException = mock(TestExceptionWithNoAnnotationAttributes.class);
      doReturn(new TestExceptionWithXmlContentType()).when(mockException).getCause();

      final ExceptionHandler mockAnnotation = mock(ExceptionHandler.class);
      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      when(sut.getAnnotationFrom(mockException)).thenReturn(mockAnnotation);
      doReturn(new ModelAndView()).when(sut).handleException(mockAnnotation, mockException, mockResponse);

      final ModelAndView view = sut.resolveException(null, mockResponse, null, mockException);

      Assert.assertTrue(sut.getAnnotatedException(mockException) instanceof TestExceptionWithNoAnnotationAttributes);
   }

   @Test public void getHandledException_ShouldReturnFirstChainedAnnotatedException_WhenThrownExceptionIsUnannotatedAndGetCauseIsTrue() throws Exception {
      TestExceptionWithNoAnnotation mockException = mock(TestExceptionWithNoAnnotation.class);
      TestExceptionWithNoAnnotationAttributes expectedException = new TestExceptionWithNoAnnotationAttributes("");
      doReturn(expectedException).when(mockException).getCause();

      final ExceptionHandler mockAnnotation = mock(ExceptionHandler.class);
      final HttpServletResponse mockResponse = mock(HttpServletResponse.class);

      final AnnotationHandler sut = spy(new AnnotationHandler());
      when(sut.getAnnotationFrom(expectedException)).thenReturn(mockAnnotation);
      doReturn(new ModelAndView()).when(sut).handleException(mockAnnotation, expectedException, mockResponse);

      Assert.assertTrue(sut.getHandledException(mockException) instanceof TestExceptionWithNoAnnotationAttributes);
   }

   @Test public void getHandledException_ShouldReturnThrownException_WhenThrownExceptionIsUnannotatedAndGetCauseIsFalse() throws Exception {
      TestExceptionWithNoAnnotation expectedException = new TestExceptionWithNoAnnotation("");

      final AnnotationHandler sut = new AnnotationHandler();
      sut.setUseGetCause(false);
      Exception actualException = sut.getHandledException(expectedException);

      Assert.assertTrue(actualException instanceof TestExceptionWithNoAnnotation);
   }
}

@ExceptionHandler()
class TestExceptionWithNoAnnotationAttributes extends Exception {
   public TestExceptionWithNoAnnotationAttributes(final String s) {
      super(s);
   }
}

@ExceptionHandler(contentType = MediaType.APPLICATION_XML_VALUE)
class TestExceptionWithXmlContentType extends Exception {
}

@ExceptionHandler(httpStatus = HttpStatus.NOT_FOUND)
class TestExceptionWithNotFoundStatusCode extends Exception {
}

@ExceptionHandler(contentType = MediaType.TEXT_PLAIN_VALUE, httpStatus = HttpStatus.NO_CONTENT)
class TestExceptionWithNoContentStatusCodeAndTextContentType extends Exception {
   public TestExceptionWithNoContentStatusCodeAndTextContentType(final String s) {
      super(s);
   }
}

class TestExceptionWithNoAnnotation extends Exception {
   public TestExceptionWithNoAnnotation(final String s) {
      super(s);
   }
}