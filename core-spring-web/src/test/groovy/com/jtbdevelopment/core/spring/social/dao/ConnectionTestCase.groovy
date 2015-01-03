package com.jtbdevelopment.core.spring.social.dao

import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.*
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

    protected static interface FakeFacebookApi {

    }

    protected static interface FakeTwitterApi {

    }

    protected static class FakeConnection<A> extends AbstractConnection<A> {
        final Long expireTime
        final String accessToken
        final String refreshToken
        final String secret

        FakeConnection(final ConnectionData data) {
            super(data, null)
            this.expireTime = data.expireTime
            this.accessToken = data.accessToken
            this.refreshToken = data.refreshToken
            this.secret = data.secret
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

    protected static class FakeFacebookConnection extends FakeConnection<FakeFacebookApi> {
        FakeFacebookConnection(final ConnectionData data) {
            super(data)
        }
    }

    protected static class FakeTwitterConnection extends FakeConnection<FakeTwitterApi> {
        FakeTwitterConnection(final ConnectionData data) {
            super(data)
        }
    }

    protected abstract static class FakeConnectionFactory<A> extends ConnectionFactory<A> {

        FakeConnectionFactory(final String providerId) {
            super(providerId, null, null)
        }
    }

    protected static class FaceFacebookConnectionFactory extends FakeConnectionFactory<FakeFacebookApi> {
        FaceFacebookConnectionFactory() {
            super(FACEBOOK)
        }

        @Override
        Connection<Object> createConnection(final ConnectionData data) {
            return new FakeFacebookConnection(data)
        }
    }

    protected static class FaceTwitterConnectionFactory extends FakeConnectionFactory<FakeTwitterApi> {
        FaceTwitterConnectionFactory() {
            super(TWITTER)
        }

        @Override
        Connection<Object> createConnection(final ConnectionData data) {
            return new FakeTwitterConnection(data)
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
                (FACEBOOK): new FaceFacebookConnectionFactory(),
                (TWITTER) : new FaceTwitterConnectionFactory()
        ]
        connectionFactoryLocator = [
                registeredProviderIds: {
                    return providers.keySet()
                },
                getConnectionFactory : {
                    Object s ->
                        if (s instanceof String) {
                            return providers[s]
                        }
                        if (s.is(FakeTwitterApi.class)) {
                            return providers[TWITTER]
                        }
                        if (s.is(FakeFacebookApi.class)) {
                            return providers[FACEBOOK]
                        }
                        null
                }
        ] as ConnectionFactoryLocator
    }
}
