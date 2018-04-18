package com.jtbdevelopment.core.mongo.spring;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Date: 1/9/15 Time: 6:36 PM
 */
public class MongoPropertiesTest {

  @Test
  public void testNoWarningsOnPopulatedWithUser() {
    MongoProperties properties = new MongoProperties("db", "host", 32, "user", "pass",
        "ACKNOWLEDGED");
    assertFalse(properties.isWarnings());
  }

  @Test
  public void testWarningsOnPopulatedWithUserNoDB() {
    MongoProperties properties = new MongoProperties("", "host", 32, "user", "pass",
        "ACKNOWLEDGED");
    assertTrue(properties.isWarnings());
  }

  @Test
  public void testWarningsOnPopulatedWithUserNoPassword() {
    MongoProperties properties = new MongoProperties("db", "host", 32, "user", "", "ACKNOWLEDGED");
    assertTrue(properties.isWarnings());
  }

  @Test
  public void testWarningsOnPopulatedWithPasswordAndNoUser() {
    MongoProperties properties = new MongoProperties("db", "host", 32, "", "pass", "ACKNOWLEDGED");
    assertTrue(properties.isWarnings());
  }

  @Test
  public void testNoWarningsOnPopulatedNoUser() {
    MongoProperties properties = new MongoProperties("db", "host", 32, "", "", "ACKNOWLEDGED");
    assertFalse(properties.isWarnings());
  }

  @Test
  public void testWarningsOnPopulatedNoUserAndNoDB() {
    MongoProperties properties = new MongoProperties("", "host", 32, "", "", "ACKNOWLEDGED");
    assertTrue(properties.isWarnings());
  }

}
