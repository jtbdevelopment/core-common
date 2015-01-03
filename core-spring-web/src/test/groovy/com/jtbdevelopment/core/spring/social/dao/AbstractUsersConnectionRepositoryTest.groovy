package com.jtbdevelopment.core.spring.social.dao

import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionData
import org.springframework.social.connect.ConnectionKey
import org.springframework.social.connect.ConnectionSignUp

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 1/2/15
 * Time: 6:37 PM
 *
 * loosely based on spring's own JdbcUsersConnectionRepositoryTest
 */
class AbstractUsersConnectionRepositoryTest extends ConnectionTestCase {

    private StringUsersConnectionRepository repository = new StringUsersConnectionRepository()

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        repository.connectionFactoryLocator = connectionFactoryLocator
        repository.textEncryptor = textEncryptor
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
        def providedUserId = '1234'
        Connection connection = [
                getKey: {
                    return new ConnectionKey(FACEBOOK, providedUserId)
                }
        ] as Connection
        repository.socialConnectionRepository = [
                findByProviderIdAndProviderUserId: {
                    String provider, String user ->
                        assert provider == FACEBOOK
                        assert user == providedUserId
                        return [
                                new StringSocialConnection(id: 'X', userId: '1', providerId: FACEBOOK, providerUserId: providedUserId),
                                new StringSocialConnection(id: 'Y', userId: '2', providerId: FACEBOOK, providerUserId: providedUserId)
                        ]
                }
        ] as AbstractSocialConnectionRepository
        assert ['1', '2'] as Set == repository.findUserIdsWithConnection(connection) as Set
    }

    public void testFindInValidUserIdWithNoSignUp() {
        def providedUserId = '1234'
        Connection connection = [
                getKey: {
                    return new ConnectionKey(FACEBOOK, providedUserId)
                }
        ] as Connection
        repository.socialConnectionRepository = [
                findByProviderIdAndProviderUserId: {
                    String provider, String user ->
                        assert provider == FACEBOOK
                        assert user == providedUserId
                        return []
                }
        ] as AbstractSocialConnectionRepository
        assert [] as Set == repository.findUserIdsWithConnection(connection) as Set
    }

    public void testFindInValidUserIdWithSuccessfulSignUp() {
        def providedUserId = '1234'
        def localUserId = '1'
        def connectionData = new ConnectionData(FACEBOOK, providedUserId, 'display', 'profile', 'image', 'at', 's', 'rt', 100L)
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"))
        Connection connection = [
                getKey    : {
                    return new ConnectionKey(FACEBOOK, providedUserId)
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
                        assert user == providedUserId
                        return []
                },
                save                             : {
                    AbstractSocialConnection sc ->
                        assertNull sc.id
                        assert sc.userId == localUserId
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
                        return localUserId
                }
        ] as ConnectionSignUp
        assert [localUserId] as Set == repository.findUserIdsWithConnection(connection) as Set
    }

    public void testFindInValidUserIdWithFailedSignUp() {
        def providerUserId = '1234'
        Connection connection = [
                getKey: {
                    return new ConnectionKey(FACEBOOK, providerUserId)
                }
        ] as Connection
        repository.textEncryptor = new ReverseEncryptor()
        repository.socialConnectionRepository = [
                findByProviderIdAndProviderUserId: {
                    String provider, String user ->
                        assert provider == FACEBOOK
                        assert user == providerUserId
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

    public void testFindConnectionsForIds() {
        def providerUserIds = ['1234', '5678', '9010'] as Set
        repository.socialConnectionRepository = [
                findByProviderIdAndProviderUserIdIn: {
                    String provider, Collection<String> userIds ->
                        assert provider == FACEBOOK
                        assert userIds == providerUserIds
                        return [
                                new StringSocialConnection(id: 'X', userId: '1', providerId: FACEBOOK, providerUserId: '1235'),
                                new StringSocialConnection(id: 'Y', userId: '2', providerId: FACEBOOK, providerUserId: '9010')
                        ]
                }
        ] as AbstractSocialConnectionRepository
        assert ['1', '2'] as Set == repository.findUserIdsConnectedTo(FACEBOOK, providerUserIds)
    }
}
