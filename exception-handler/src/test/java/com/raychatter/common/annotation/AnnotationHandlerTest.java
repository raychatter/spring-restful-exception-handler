package com.raychatter.common.annotation;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

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

   @Test public void formatDefaultMessage_ShouldRenderDefaultUserTemplate_WhenNoUserTemplateGiven() throws Exception {
      final String defaultTemplate = "DEFAULT TEMPLATE: %s";
      final String exceptionMessage = "AN ERROR MESSAGE";
      final String expectedMessage = "DEFAULT TEMPLATE: AN ERROR MESSAGE";

      AnnotationHandler spyAnnotationHandler = spy(new AnnotationHandler());
      doReturn(defaultTemplate).when(spyAnnotationHandler).readDefaultTemplate();

      final String actual = spyAnnotationHandler.formatDefaultMessage(new Exception(exceptionMessage));

      Assert.assertEquals(expectedMessage, actual);
   }

   @Test public void readDefaultTemplate_ShouldCatchIOException_WhenDefaultTemplateNotFound() throws Exception {
      final String expectedTemplate = "Error: %s";
      final String defaultFilepath = "defaults/default.template";

      AnnotationHandler spyAnnotationHandler = spy(new AnnotationHandler());
      doThrow(new IOException()).when(spyAnnotationHandler).getInputStream(defaultFilepath);

      final String actualTemplate = spyAnnotationHandler.readDefaultTemplate();

      Assert.assertEquals(expectedTemplate, actualTemplate);
   }
}