package com.jtbdevelopment.core.spring;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 1/9/15 Time: 7:01 PM
 */
public class CoreSpringConfigurationTest {

  private CoreSpringConfiguration configuration = new CoreSpringConfiguration();

  @Test
  public void testPropertyPlaceHolder() {
    PropertySourcesPlaceholderConfigurer p = configuration.propertyPlaceholderConfigurer();
    assertTrue((boolean) ReflectionTestUtils.getField(p, "ignoreResourceNotFound"));
  }

  @Test
  public void testClassAnnotations() {
    assertTrue(CoreSpringConfiguration.class.isAnnotationPresent(Configuration.class));
    assertArrayEquals(new ArrayList<String>(Arrays.asList("com.jtbdevelopment")).toArray(),
        CoreSpringConfiguration.class.getAnnotation(ComponentScan.class).basePackages());
  }

  @Test
  public void testPropertyPlaceHolderAnnotations() throws NoSuchMethodException {
    assertTrue(CoreSpringConfiguration.class.getMethod("propertyPlaceholderConfigurer")
        .isAnnotationPresent(Bean.class));
  }
}
