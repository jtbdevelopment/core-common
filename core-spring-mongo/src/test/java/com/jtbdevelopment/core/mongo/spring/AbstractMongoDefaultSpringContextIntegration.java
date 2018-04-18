package com.jtbdevelopment.core.mongo.spring;

import com.jtbdevelopment.core.spring.CoreSpringConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Date: 1/4/2015 Time: 7:14 PM
 */
public abstract class AbstractMongoDefaultSpringContextIntegration extends
    AbstractMongoNoSpringContextIntegration {

  protected static ApplicationContext context;

  @BeforeClass
  public static void setupSpringAndMongo() throws Exception {
    setupMongo();
    context = new AnnotationConfigApplicationContext(CoreSpringConfiguration.class);
  }

  @AfterClass
  public static void tearDownSpringAndMongo() throws Exception {
    tearDownMongo();
  }
}
