package com.jtbdevelopment.core.mongo.spring.security.rememberme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Date;
import junit.framework.TestCase;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * Date: 1/2/15 Time: 6:02 PM
 */
public class MongoRememberMeTokenTest {

  @Test
  public void testNewTokenFromExistingGenericToken() {
    PersistentRememberMeToken from = new PersistentRememberMeToken("u", "s", "t", new Date());
    MongoRememberMeToken to = new MongoRememberMeToken(from);

    TestCase.assertNull(to.getId());
    assertEquals(from.getUsername(), to.getUsername());
    assertEquals(from.getSeries(), to.getSeries());
    assertEquals(from.getTokenValue(), to.getTokenValue());
    assertEquals(from.getDate(), to.getDate());
  }

  @Test
  public void testNewTokenFromValues() {
    String s = "s";
    String tv = "t";
    String u = "u";
    Date d = new Date();
    ObjectId id = new ObjectId();

    MongoRememberMeToken to = new MongoRememberMeToken(u, s, tv, d, id);
    assertEquals(u, to.getUsername());
    assertEquals(s, to.getSeries());
    assertEquals(tv, to.getTokenValue());
    assertEquals(d, to.getDate());
    assertEquals(id, to.getId());
  }

  @Test
  public void testIdAnnotation() throws NoSuchFieldException {
    Field m = MongoRememberMeToken.class.getDeclaredField("id");
    assertNotNull(m);
    Id i = m.getAnnotation(Id.class);
    assertNotNull(i);
  }

  @Test
  public void testClassAnnotations() {
    Document d = MongoRememberMeToken.class.getAnnotation(Document.class);
    assertNotNull(d);
    assertEquals("rememberMeToken", d.collection());
    CompoundIndexes ci = MongoRememberMeToken.class.getAnnotation(CompoundIndexes.class);
    assertNotNull(ci);
    assertEquals(1, ci.value().length);
    assertTrue(ci.value()[0].unique());
    assertEquals("series", ci.value()[0].name());
    assertEquals("{'series':1}", ci.value()[0].def());
  }

}
