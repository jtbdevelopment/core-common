package com.jtbdevelopment.core.spring.social.dao

import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionData
import org.springframework.social.connect.ConnectionFactory
import org.springframework.social.connect.ConnectionFactoryLocator
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.support.AbstractConnection

/**
 * Date: 1/3/2015
 * Time: 12:10 PM
 */
abstract class ConnectionTestCase extends GroovyTestCase {
    protected static final String FACEBOOK = 'facebook'
    protected static final String TWITTER = 'twitter'
    protected static final String NEWCO = 'newco'

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

    protected static class FakeConnection extends AbstractConnection<Object> {
        FakeConnection(final ConnectionData data) {
            super(data, null)
        }

        @Override
        ConnectionData createData() {
            return null
        }

        @Override
        Object getApi() {
            return null
        }
    }

    protected static class FakeConnectionFactory extends ConnectionFactory<Object> {

        FakeConnectionFactory() {
            super(null, null, null)
        }

        @Override
        Connection<Object> createConnection(final ConnectionData data) {
            return new FakeConnection(data)
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

    protected Map<String, FakeConnectionFactory> providers;
    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected TextEncryptor textEncryptor = new ReverseEncryptor()

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        providers = [
                (FACEBOOK): new FakeConnectionFactory(),
                (TWITTER) : new FakeConnectionFactory()
        ]
        connectionFactoryLocator = [
                registeredProviderIds: {
                    return providers.keySet()
                },
                getConnectionFactory : {
                    String s ->
                        return providers[s]
                }
        ] as ConnectionFactoryLocator
    }
}
