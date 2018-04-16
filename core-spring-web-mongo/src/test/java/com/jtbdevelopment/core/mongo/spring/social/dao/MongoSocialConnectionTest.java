package com.jtbdevelopment.core.mongo.spring.social.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.core.mongo.spring.security.rememberme.MongoRememberMeToken;
import java.lang.reflect.Field;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Date: 1/3/2015 Time: 12:00 PM
 */
public class MongoSocialConnectionTest {

  @Test
  public void testClassAnnotations() {
    Document d = MongoSocialConnection.class.getAnnotation(Document.class);
    assertNotNull(d);
    assertEquals("socialConnections", d.collection());
    CompoundIndexes ci = MongoSocialConnection.class.getAnnotation(CompoundIndexes.class);
    assertNotNull(ci);
    assertEquals(2, ci.value().length);
    CompoundIndex i = ci.value()[0];
    assertEquals("{'userId': 1, 'providerId': 1, 'created': 1}", i.def());
    assertTrue(i.unique());
    assertEquals("sc_uidpidc", i.name());
    i = ci.value()[1];
    assertEquals("{'userId': 1, 'providerId': 1, 'providerUserId': 1}", i.def());
    assertTrue(i.unique());
    assertEquals("sc_pk", i.name());
  }

  @Test
  public void testIdAnnotation() throws NoSuchFieldException {
    Field m = MongoRememberMeToken.class.getDeclaredField("id");
    assertNotNull(m);
    Id i = m.getAnnotation(Id.class);
    assertNotNull(i);
  }

}
