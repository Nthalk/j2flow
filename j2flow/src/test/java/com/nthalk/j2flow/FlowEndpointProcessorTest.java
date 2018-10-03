package com.nthalk.j2flow;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.junit.Test;

public class FlowEndpointProcessorTest {

  @Test
  public void test() {
    FlowEndpointProcessor flowEndpointProcessor = new FlowEndpointProcessor();
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource("test/TestController.java"))
        .processedWith(flowEndpointProcessor)
        .compilesWithoutError();
  }
}
