package com.jtbdevelopment.core.mongo.spring;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Date: 1/9/15 Time: 6:51 PM
 */
public class MongoConfigurationTest {

  @Test
  public void testClassAnnotations() {
    assertNotNull(MongoConfiguration.class.getAnnotation(Configuration.class));
    assertNotNull(MongoConfiguration.class.getAnnotation(EnableMongoAuditing.class));
    assertArrayEquals(Collections.singletonList("com.jtbdevelopment").toArray(),
        MongoConfiguration.class.getAnnotation(EnableMongoRepositories.class).value());
  }

}
