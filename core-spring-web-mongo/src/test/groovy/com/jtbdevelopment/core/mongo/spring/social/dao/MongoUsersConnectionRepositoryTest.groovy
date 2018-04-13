package com.jtbdevelopment.core.mongo.spring.social.dao

import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.ConnectionFactoryLocator
import org.springframework.test.util.ReflectionTestUtils

import static org.mockito.Mockito.mock

/**
 * Date: 1/3/2015
 * Time: 11:51 AM
 */
class MongoUsersConnectionRepositoryTest extends GroovyTestCase {
    MongoSocialConnectionRepository connectionRepository = mock(MongoSocialConnectionRepository.class)
    ConnectionFactoryLocator locator = mock(ConnectionFactoryLocator.class)
    TextEncryptor textEncryptor = mock(TextEncryptor.class)
    MongoUsersConnectionRepository repository = new MongoUsersConnectionRepository(null, connectionRepository, locator, textEncryptor)

    void testCreateConnectionRepository() {
        String anId = 'TADA!'
        MongoConnectionRepository r = repository.createConnectionRepository(anId)
        assert textEncryptor.is(ReflectionTestUtils.getField(r, "encryptor"))
        assert connectionRepository.is(ReflectionTestUtils.getField(r, "socialConnectionRepository"))
        assert locator.is(ReflectionTestUtils.getField(r, "connectionFactoryLocator"))
        assert anId == ReflectionTestUtils.getField(r, "userId")
    }

    void testCreateConnectionRepositoryWithNull() {
        shouldFail(IllegalArgumentException.class) {
            repository.createConnectionRepository(null)
        }
    }

    void testCreateConnectionRepositoryWithBlank() {
        shouldFail(IllegalArgumentException.class) {
            repository.createConnectionRepository('')
        }
    }
}
