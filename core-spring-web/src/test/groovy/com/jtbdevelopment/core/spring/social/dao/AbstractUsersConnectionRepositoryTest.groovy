package com.jtbdevelopment.core.spring.social.dao

import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.*

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 1/2/15
 * Time: 6:37 PM
 */
class AbstractUsersConnectionRepositoryTest extends GroovyTestCase {
    public static final String FACEBOOK = 'facebook'

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

    private StringUsersConnectionRepository repository = new StringUsersConnectionRepository()
    private Map<String, FakeConnectionFactory> providers;

    @Override
    protected void setUp() throws Exception {
        providers = [
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
        repository.textEncryptor = new ReverseEncryptor()
    }

    void testSetupInitializesConnectionRepositoryStatics() {
        StringConnectionRepository.encryptor = null
        StringConnectionRepository.connectionFactoryLocator = null
        StringConnectionRepository.socialConnectionRepository = null
        StringConnectionRepository.providerConnectionFactoryMap = [:]

        repository.connectionSignUp = [] as ConnectionSignUp
        repository.socialConnectionRepository = [] as AbstractSocialConnectionRepository

        repository.setUp()

        assert StringConnectionRepository.providerConnectionFactoryMap == providers
        assert repository.connectionFactoryLocator.is(StringConnectionRepository.connectionFactoryLocator)
        assert repository.socialConnectionRepository.is(StringConnectionRepository.socialConnectionRepository)
        assert repository.textEncryptor.is(StringConnectionRepository.encryptor)
    }

    public void testFindValidUserIdWithValidConnectionFactory() {
        def puid = '1234'
        Connection connection = [
                getKey: {
                    return new ConnectionKey(FACEBOOK, puid)
                }
        ] as Connection
        repository.socialConnectionRepository = [
                findByProviderIdAndProviderUserId: {
                    String provider, String user ->
                        assert provider == FACEBOOK
                        assert user == puid
                        return [
                                new StringSocialConnection(id: 'X', userId: '1', providerId: FACEBOOK, providerUserId: puid),
                                new StringSocialConnection(id: 'Y', userId: '2', providerId: FACEBOOK, providerUserId: puid)
                        ]
                }
        ] as AbstractSocialConnectionRepository
        assert ['1', '2'] as Set == repository.findUserIdsWithConnection(connection) as Set
    }

    public void testFindInValidUserIdWithNoSignUp() {
        def puid = '1234'
        Connection connection = [
                getKey: {
                    return new ConnectionKey(FACEBOOK, puid)
                }
        ] as Connection
        repository.socialConnectionRepository = [
                findByProviderIdAndProviderUserId: {
                    String provider, String user ->
                        assert provider == FACEBOOK
                        assert user == puid
                        return []
                }
        ] as AbstractSocialConnectionRepository
        assert [] as Set == repository.findUserIdsWithConnection(connection) as Set
    }

    public void testFindInValidUserIdWithSuccessfulSignUp() {
        def puid = '1234'
        def uid = '1'
        def connectionData = new ConnectionData(FACEBOOK, puid, 'display', 'profile', 'image', 'at', 's', 'rt', 100L)
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"))
        Connection connection = [
                getKey    : {
                    return new ConnectionKey(FACEBOOK, puid)
                },
                createData: {
                    return connectionData
                }
        ] as Connection
        repository.textEncryptor = new ReverseEncryptor()
        repository.socialConnectionRepository = [
                findByProviderIdAndProviderUserId: {
                    String provider, String user ->
                        assert provider == FACEBOOK
                        assert user == puid
                        return []
                },
                save                             : {
                    AbstractSocialConnection sc ->
                        assertNull sc.id
                        assertNull sc.version
                        assert sc.userId == uid
                        assert sc.providerUserId == connectionData.providerUserId
                        assert sc.created.compareTo(now) > 0
                        assert sc.displayName == connectionData.displayName
                        assert sc.expireTime == connectionData.expireTime
                        assert sc.imageUrl == connectionData.imageUrl
                        assert sc.profileUrl == connectionData.profileUrl
                        assert sc.providerId == connectionData.providerId
                        assert sc.accessToken == connectionData.accessToken.reverse()
                        assert sc.refreshToken == connectionData.refreshToken.reverse()
                        assert sc.secret == connectionData.secret.reverse()
                        return sc
                }
        ] as AbstractSocialConnectionRepository
        StringConnectionRepository.socialConnectionRepository = repository.socialConnectionRepository
        StringConnectionRepository.encryptor = repository.textEncryptor
        repository.connectionSignUp = [
                execute: {
                    Connection c ->
                        assert c.is(connection)
                        return uid
                }
        ] as ConnectionSignUp
        assert [uid] as Set == repository.findUserIdsWithConnection(connection) as Set
    }

    public void testFindInValidUserIdWithFailedSignUp() {
        def puid = '1234'
        Connection connection = [
                getKey    : {
                    return new ConnectionKey(FACEBOOK, puid)
                }
        ] as Connection
        repository.textEncryptor = new ReverseEncryptor()
        repository.socialConnectionRepository = [
                findByProviderIdAndProviderUserId: {
                    String provider, String user ->
                        assert provider == FACEBOOK
                        assert user == puid
                        return []
                }
        ] as AbstractSocialConnectionRepository
        StringConnectionRepository.socialConnectionRepository = repository.socialConnectionRepository
        StringConnectionRepository.encryptor = repository.textEncryptor
        repository.connectionSignUp = [
                execute: {
                    Connection c ->
                        assert c.is(connection)
                        return null
                }
        ] as ConnectionSignUp
        assert [] as Set == repository.findUserIdsWithConnection(connection) as Set
    }
}
