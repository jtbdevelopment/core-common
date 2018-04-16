package com.jtbdevelopment.core.mongo.spring.social.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 1/3/2015 Time: 11:59 AM
 */
public class MongoConnectionRepositoryTest {

  private String id = "TESTID";
  private MongoConnectionRepository repository = new MongoConnectionRepository(null, null, null,
      id);

  @Test
  public void testConstructor() {
    assertEquals(id, ReflectionTestUtils.getField(repository, "userId"));
  }

  @Test
  public void testCreateSocialConnection() {
    assertTrue(repository.createSocialConnection() instanceof MongoSocialConnection);
  }
}
