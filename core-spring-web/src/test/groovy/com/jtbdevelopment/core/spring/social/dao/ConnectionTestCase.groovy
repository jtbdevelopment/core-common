package com.jtbdevelopment.core.spring.social.dao

import com.jtbdevelopment.core.spring.social.dao.utility.*
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.ConnectionFactoryLocator
import org.springframework.social.connect.ConnectionRepository

/**
 * Date: 1/3/2015
 * Time: 12:10 PM
 */
abstract class ConnectionTestCase extends GroovyTestCase {

    protected static final String NEWCO = 'newco'

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
                (FakeFacebookApi.FACEBOOK): new FakeFacebookConnectionFactory(),
                (FakeTwitterApi.TWITTER)  : new FakeTwitterConnectionFactory()
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
                            return providers[FakeTwitterApi.TWITTER]
                        }
                        if (s.is(FakeFacebookApi.class)) {
                            return providers[FakeFacebookApi.FACEBOOK]
                        }
                        null
                }
        ] as ConnectionFactoryLocator
    }
}
