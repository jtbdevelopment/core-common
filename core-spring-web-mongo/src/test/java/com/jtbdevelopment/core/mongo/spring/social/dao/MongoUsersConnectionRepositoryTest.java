package com.jtbdevelopment.core.mongo.spring.social.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 1/3/2015 Time: 11:51 AM
 */
public class MongoUsersConnectionRepositoryTest {

  private MongoSocialConnectionRepository connectionRepository = mock(
      MongoSocialConnectionRepository.class);
  private ConnectionFactoryLocator locator = mock(ConnectionFactoryLocator.class);
  private TextEncryptor textEncryptor = mock(TextEncryptor.class);
  private MongoUsersConnectionRepository repository = new MongoUsersConnectionRepository(null,
      connectionRepository, locator, textEncryptor);

  @Test
  public void testCreateConnectionRepository() {
    String anId = "TADA!";
    MongoConnectionRepository r = (MongoConnectionRepository) repository
        .createConnectionRepository(anId);
    assertEquals(textEncryptor, ReflectionTestUtils.getField(r, "encryptor"));
    assertEquals(connectionRepository,
        ReflectionTestUtils.getField(r, "socialConnectionRepository"));
    assertEquals(locator, ReflectionTestUtils.getField(r, "connectionFactoryLocator"));
    assertEquals(anId, ReflectionTestUtils.getField(r, "userId"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateConnectionRepositoryWithNull() {
    repository.createConnectionRepository(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateConnectionRepositoryWithBlank() {
    repository.createConnectionRepository("");
  }
}
