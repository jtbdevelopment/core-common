package com.jtbdevelopment.core.spring.social.dao

import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionData
import org.springframework.social.connect.ConnectionFactory
import org.springframework.social.connect.ConnectionRepository

/**
 * Date: 1/3/2015
 * Time: 12:10 PM
 */
class ConnectionTestCase extends GroovyTestCase {
    protected static final String FACEBOOK = 'facebook'
    protected static final String TWITTER = 'twitter'

    protected static class ReverseEncryptor implements TextEncryptor {
        @Override
        String decrypt(final String encryptedText) {
            return encryptedText.reverse()
        }

        @Override
        String encrypt(final String text) {
            return text.reverse()
        }
    }

    protected static class FakeConnectionFactory extends ConnectionFactory<String> {

        FakeConnectionFactory() {
            super(null, null, null)
        }

        @Override
        Connection<String> createConnection(final ConnectionData data) {
            return null
        }
    }

    protected static class StringConnectionRepository extends AbstractConnectionRepository {
        StringConnectionRepository(final String userId) {
            super(userId)
        }

        @Override
        SocialConnection createSocialConnection() {
            return new StringSocialConnection()
        }
    }

    protected static class StringUsersConnectionRepository extends AbstractUsersConnectionRepository {
        @Override
        ConnectionRepository createConnectionRepository(final String userId) {
            return new StringConnectionRepository(userId)
        }
    }

    protected static class StringSocialConnection extends AbstractSocialConnection<String> {
        String id
    }
}
