package com.jtbdevelopment.core.spring.social.dao

import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.*

/**
 * Date: 1/2/15
 * Time: 6:37 PM
 */
class AbstractUsersConnectionRepositoryTest extends GroovyTestCase {
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
        SocialConnection createSocialConnectionFromData(final ConnectionData data) {
            return null
        }
    }

    protected static class StringUsersConnectionRepository extends AbstractUsersConnectionRepository {
        @Override
        ConnectionRepository createConnectionRepository(final String userId) {
            return null
        }
    }

    private StringUsersConnectionRepository repository = new StringUsersConnectionRepository()

    void testSetupInitializesConnectionRepositoryStatics() {
        def providers = [
                'facebook': new FakeConnectionFactory(),
                'twitter' : new FakeConnectionFactory()
        ]
        repository.connectionFactoryLocator = [
                registeredProviderIds: {
                    return providers.keySet()
                },
                getConnectionFactory : {
                    String s ->
                        return providers[s]
                }
        ] as ConnectionFactoryLocator
        repository.connectionSignUp = [] as ConnectionSignUp
        repository.userConnectionRepository = [] as AbstractSocialConnectionRepository
        repository.textEncryptor = [] as TextEncryptor

        assertNull StringConnectionRepository.connectionFactoryLocator
        assertNull StringConnectionRepository.userConnectionRepository
        assertNull StringConnectionRepository.encryptor
        assert StringConnectionRepository.providerConnectionFactoryMap == [:]
        repository.setUp()

        assert StringConnectionRepository.providerConnectionFactoryMap == providers
        assert repository.connectionFactoryLocator.is(StringConnectionRepository.connectionFactoryLocator)
        assert repository.userConnectionRepository.is(StringConnectionRepository.userConnectionRepository)
        assert repository.textEncryptor.is(StringConnectionRepository.encryptor)
    }
}
