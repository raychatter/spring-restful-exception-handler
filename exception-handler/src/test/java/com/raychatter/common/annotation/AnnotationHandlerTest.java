package com.raychatter.common.annotation;

import junit.framework.TestCase;
import org.junit.Test;

public class AnnotationHandlerTest extends TestCase {

   @Test
   public void shouldReturnModelAndViewIfAnnotationIsNull() {
      AnnotationHandler annotationHandler = new AnnotationHandler();
   }

}
