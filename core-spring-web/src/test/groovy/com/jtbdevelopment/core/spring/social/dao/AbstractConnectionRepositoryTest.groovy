package com.jtbdevelopment.core.spring.social.dao

import com.jtbdevelopment.core.spring.social.dao.utility.FakeConnection
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookConnection
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterApi
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.dao.DuplicateKeyException
import org.springframework.social.connect.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

import static com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi.FACEBOOK
import static com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterApi.TWITTER
import static org.mockito.Matchers.isA
import static org.mockito.Mockito.*

/**
 * Date: 1/3/2015
 * Time: 12:09 PM
 *
 * Loosely based on spring's own JdbcUsersConnectionRepositoryTest
 */
class AbstractConnectionRepositoryTest extends ConnectionTestCase {
    private static String TESTID = 'TESTID'
    private
    static FACEBOOK1SC = new StringSocialConnection(userId: TESTID, id: 'L1', providerId: FACEBOOK, providerUserId: 'F1', profileUrl: 'F1P', imageUrl: 'F1I', accessToken: 'F1A', secret: 'F1S', expireTime: 1, displayName: 'F1DN')
    private
    static FACEBOOK2SC = new StringSocialConnection(userId: TESTID, id: 'L1', providerId: FACEBOOK, providerUserId: 'F2', profileUrl: 'F2P', imageUrl: 'F2I', accessToken: 'F2A', secret: 'F2S', expireTime: 2, displayName: 'F2DN')
    private
    static FACEBOOK2SCDUPE = new StringSocialConnection(userId: TESTID, id: 'L1DUPE', providerId: FACEBOOK, providerUserId: 'F2', profileUrl: 'F2PDUPE', imageUrl: 'F2IDUPE', accessToken: 'F2A', secret: 'F2S', expireTime: 2, displayName: 'F2DNDUPE')
    private
    static TWITTER1SC = new StringSocialConnection(userId: TESTID, id: 'L1', providerId: TWITTER, providerUserId: 'T1', profileUrl: 'T1P', imageUrl: 'T1I', accessToken: 'T1A', refreshToken: 'T1RT', expireTime: 1, displayName: 'T1DN')
    private
    static NEWCO1SC = new StringSocialConnection(userId: TESTID, id: 'L1', providerId: NEWCO, providerUserId: 'N1', profileUrl: 'N1P', imageUrl: 'N1I', expireTime: 2, displayName: 'N1DN')

    private AbstractSocialConnectionRepository socialConnectionRepository = mock(AbstractSocialConnectionRepository.class)
    private StringConnectionRepository repository

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        AbstractConnectionRepository.providerConnectionFactoryMap.clear()
        repository = new StringConnectionRepository(socialConnectionRepository, connectionFactoryLocator, textEncryptor, TESTID)
    }

    void testSetupInitializesConnectionRepositoryStaticsFirstTime() {
        assert providers == AbstractConnectionRepository.providerConnectionFactoryMap

        connectionFactoryLocator = mock(ConnectionFactoryLocator.class)
        when(connectionFactoryLocator.registeredProviderIds()).thenReturn([FACEBOOK] as Set)
        def factory = mock(ConnectionFactory.class)
        when(connectionFactoryLocator.getConnectionFactory(FACEBOOK)).thenReturn(factory)
        repository = new StringConnectionRepository(socialConnectionRepository, connectionFactoryLocator, textEncryptor, TESTID)

        assert providers == AbstractConnectionRepository.providerConnectionFactoryMap
    }

    void testSortDefinitions() {
        assert StringConnectionRepository.SORT_PID_CREATED.toString() == 'providerId: ASC,created: ASC'
        assert StringConnectionRepository.SORT_CREATED.toString() == 'created: ASC'
    }

    void testFindAllConnectionsWithValidProviders() {
        when(socialConnectionRepository.findByUserId(TESTID, AbstractConnectionRepository.SORT_PID_CREATED)).thenReturn([FACEBOOK1SC, FACEBOOK2SC, TWITTER1SC])
        MultiValueMap<String, Connection<?>> r = repository.findAllConnections()
        assert r.containsKey(FACEBOOK)
        assert r[FACEBOOK].size() == 2
        compareConnectionToSocialConnection(r[FACEBOOK][0], FACEBOOK1SC)
        compareConnectionToSocialConnection(r[FACEBOOK][1], FACEBOOK2SC)
        assert r.containsKey(TWITTER)
        assert r[TWITTER].size() == 1
        compareConnectionToSocialConnection(r[TWITTER][0], TWITTER1SC)
    }

    void testFindAllConnectionsWithEmptyResults() {
        when(socialConnectionRepository.findByUserId(TESTID, AbstractConnectionRepository.SORT_PID_CREATED)).thenReturn([])
        MultiValueMap<String, Connection<?>> r = repository.findAllConnections()
        assert r.containsKey(FACEBOOK)
        assert r[FACEBOOK].size() == 0
        assert r.containsKey(TWITTER)
        assert r[TWITTER].size() == 0
    }

    void testFindAllConnectionsWithInvalidProviders() {
        when(socialConnectionRepository.findByUserId(TESTID, AbstractConnectionRepository.SORT_PID_CREATED)).thenReturn([FACEBOOK1SC, FACEBOOK2SC, TWITTER1SC, NEWCO1SC])
        shouldFail(Exception.class) {
            repository.findAllConnections()
        }
    }

    void testFindConnectionsByProviderId() {
        when(socialConnectionRepository.findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED)).thenReturn([FACEBOOK2SC, FACEBOOK1SC])
        List<Connection<?>> connections = repository.findConnections(FACEBOOK)
        assert connections.size() == 2
        compareConnectionToSocialConnection(connections[0], FACEBOOK2SC)
        compareConnectionToSocialConnection(connections[1], FACEBOOK1SC)
    }

    void testFindConnectionsByProviderIdWithEmptyResults() {
        when(socialConnectionRepository.findByUserIdAndProviderId(TESTID, NEWCO, AbstractConnectionRepository.SORT_CREATED)).thenReturn([])
        List<Connection<?>> connections = repository.findConnections(NEWCO)
        assert connections.size() == 0
    }

    void testFindConnectionsByProviderApi() {
        when(socialConnectionRepository.findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED)).thenReturn([FACEBOOK2SC, FACEBOOK1SC])
        List<Connection<?>> connections = repository.findConnections(FakeFacebookApi.class)
        assert connections.size() == 2
        compareConnectionToSocialConnection(connections[0], FACEBOOK2SC)
        compareConnectionToSocialConnection(connections[1], FACEBOOK1SC)
    }

    void testFindConnectionsByProviderApiWithEmptyResults() {
        when(socialConnectionRepository.findByUserIdAndProviderId(TESTID, TWITTER, AbstractConnectionRepository.SORT_CREATED)).thenReturn([])
        List<Connection<?>> connections = repository.findConnections(FakeTwitterApi.class)
        assert connections.size() == 0
    }

    void testFindConnectionsToProviderUserIds() {
        def TPIDS = ["DONTEXIST", TWITTER1SC.providerUserId]
        def FBPIDS = [FACEBOOK1SC.providerUserId, FACEBOOK2SC.providerUserId]
        MultiValueMap<String, String> input = new LinkedMultiValueMap<>()
        input.put(FACEBOOK, FBPIDS)
        input.put(TWITTER, TPIDS)
        // Tests order of results from DB only important with dupe provider user ids)
        when(socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserIdIn(TESTID, FACEBOOK, FBPIDS, AbstractConnectionRepository.SORT_PID_CREATED)).thenReturn([FACEBOOK2SC, FACEBOOK1SC, FACEBOOK2SCDUPE])
        // Tests only one id coming back, plus an irrelevant one
        when(socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserIdIn(TESTID, TWITTER, TPIDS, AbstractConnectionRepository.SORT_PID_CREATED)).thenReturn([TWITTER1SC, NEWCO1SC])
        MultiValueMap<String, Connection<?>> result = repository.findConnectionsToUsers(input)
        assert result.size() == 2
        List<Connection<?>> fb = result.get(FACEBOOK)
        assert fb && fb.size() == 2
        compareConnectionToSocialConnection(fb[0], FACEBOOK1SC)
        compareConnectionToSocialConnection(fb[1], FACEBOOK2SCDUPE)
        List<Connection<?>> t = result.get(TWITTER)
        assert t && t.size() == 2
        assert t[0] == null
        compareConnectionToSocialConnection(t[1], TWITTER1SC)
    }

    void testFindConnectionsToProviderUserIdsWithNullInput() {
        MultiValueMap<String, Connection<?>> result = repository.findConnectionsToUsers(null)
        assert result.isEmpty()
    }

    void testFindConnectionsToProviderUserIdsWithEmptyInput() {
        MultiValueMap<String, String> input = new LinkedMultiValueMap<>()
        MultiValueMap<String, Connection<?>> result = repository.findConnectionsToUsers(input)
        assert result.isEmpty()
    }

    void testFindByConnectionKey() {
        when(socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(TESTID, TWITTER, TWITTER1SC.providerUserId)).thenReturn(TWITTER1SC)
        compareConnectionToSocialConnection(repository.getConnection(new ConnectionKey(TWITTER, TWITTER1SC.providerUserId)), TWITTER1SC)
    }

    void testFindByConnectionKeyNotFound() {
        when(socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(TESTID, TWITTER, TWITTER1SC.providerId)).thenReturn(null)
        shouldFail(NoSuchConnectionException.class) {
            repository.getConnection(new ConnectionKey(TWITTER, TWITTER1SC.providerUserId))
        }
    }

    void testFindByAPIAndPID() {
        when(socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(TESTID, FACEBOOK, FACEBOOK2SC.providerUserId)).thenReturn(FACEBOOK2SC)
        compareConnectionToSocialConnection(repository.getConnection(FakeFacebookApi.class, FACEBOOK2SC.providerUserId), FACEBOOK2SC)
    }

    void testFindByAPIAndPIDNotFound() {
        when(socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(TESTID, FACEBOOK, FACEBOOK2SC.providerUserId)).thenReturn(null)
        shouldFail(NoSuchConnectionException.class) {
            repository.getConnection(FakeFacebookApi.class, FACEBOOK2SC.providerUserId)
        }
    }

    void testGetPrimaryConnection() {
        when(socialConnectionRepository.findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED)).thenReturn([FACEBOOK2SC, FACEBOOK1SC])
        compareConnectionToSocialConnection(repository.getPrimaryConnection(FakeFacebookApi.class), FACEBOOK2SC)
    }

    void testGetPrimaryConnectionNoResults() {
        when(socialConnectionRepository.findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED)).thenReturn([])
        shouldFail(NotConnectedException.class) {
            repository.getPrimaryConnection(FakeFacebookApi.class)
        }
    }

    void testFindPrimaryConnection() {
        when(socialConnectionRepository.findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED)).thenReturn([FACEBOOK2SC, FACEBOOK1SC])
        compareConnectionToSocialConnection(repository.findPrimaryConnection(FakeFacebookApi.class), FACEBOOK2SC)
    }

    void testFindPrimaryConnectionNoResults() {
        when(socialConnectionRepository.findByUserIdAndProviderId(TESTID, FACEBOOK, StringConnectionRepository.SORT_CREATED)).thenReturn([])
        assertNull repository.findPrimaryConnection(FakeFacebookApi.class)
    }

    void testRemoveConnections() {
        when(socialConnectionRepository.deleteByUserIdAndProviderId(TESTID, TWITTER)).thenReturn(1L)
        repository.removeConnections(TWITTER)
        verify(socialConnectionRepository).deleteByUserIdAndProviderId(TESTID, TWITTER)
    }

    void testRemoveConnection() {
        when(socialConnectionRepository.deleteByUserIdAndProviderIdAndProviderUserId(TESTID, TWITTER, TWITTER1SC.providerUserId)).thenReturn(1L)
        repository.removeConnection(new ConnectionKey(TWITTER, TWITTER1SC.providerUserId))
        verify(socialConnectionRepository).deleteByUserIdAndProviderIdAndProviderUserId(TESTID, TWITTER, TWITTER1SC.providerUserId)
    }

    void testAddConnection() {
        when(socialConnectionRepository.save(isA(SocialConnection.class))).then(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                SocialConnection sc = invocation.arguments[0]
                assertNull sc.id
                assert sc.providerId == FACEBOOK
                assert sc.providerUserId == FACEBOOK1SC.providerUserId
                assert sc.profileUrl == FACEBOOK1SC.profileUrl
                assert sc.imageUrl == FACEBOOK1SC.imageUrl
                assert sc.displayName == FACEBOOK1SC.displayName
                assert sc.expireTime == FACEBOOK1SC.expireTime
                assert sc.accessToken == FACEBOOK1SC.accessToken.reverse()
                assert sc.refreshToken == null
                assert sc.secret == FACEBOOK1SC.secret.reverse()
                assert sc.userId == TESTID
                return sc
            }
        })
        repository.addConnection(
                new FakeFacebookConnection(
                        new ConnectionData(
                                FACEBOOK,
                                FACEBOOK1SC.providerUserId,
                                FACEBOOK1SC.displayName,
                                FACEBOOK1SC.profileUrl,
                                FACEBOOK1SC.imageUrl,
                                FACEBOOK1SC.accessToken,
                                FACEBOOK1SC.secret,
                                FACEBOOK1SC.refreshToken,
                                FACEBOOK1SC.expireTime)))
        verify(socialConnectionRepository).save(isA(SocialConnection.class))
    }

    void testAddConnectionDuplicate() {
        when(socialConnectionRepository.save(isA(SocialConnection.class))).thenThrow(new DuplicateKeyException('dupe'))

        shouldFail(DuplicateConnectionException.class) {
            repository.addConnection(
                    new FakeFacebookConnection(
                            new ConnectionData(
                                    FACEBOOK,
                                    FACEBOOK1SC.providerUserId,
                                    FACEBOOK1SC.displayName,
                                    FACEBOOK1SC.profileUrl,
                                    FACEBOOK1SC.imageUrl,
                                    FACEBOOK1SC.accessToken,
                                    FACEBOOK1SC.secret,
                                    FACEBOOK1SC.refreshToken,
                                    FACEBOOK1SC.expireTime)))
        }
    }

    void testUpdateConnection() {
        boolean saveCalled = false
        String newProfile = 'newprofile'
        String newImage = 'newimage'
        String newRefreshToken = 'newrefresh'
        String newSecret = 'newsecret'
        String newAccessToken = 'na'
        String newDisplayName = 'newdisplay'
        Long newExpire = 151L
        when(socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(TESTID, FACEBOOK, FACEBOOK2SC.providerUserId)).thenReturn(FACEBOOK2SC)
        when(socialConnectionRepository.save(isA(SocialConnection.class))).then(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                SocialConnection sc = invocation.arguments[0]
                assert sc.id == FACEBOOK2SC.id
                assert sc.providerId == FACEBOOK
                assert sc.providerUserId == FACEBOOK2SC.providerUserId
                assert sc.profileUrl == newProfile
                assert sc.imageUrl == newImage
                assert sc.displayName == newDisplayName
                assert sc.expireTime == newExpire
                assert sc.accessToken == newAccessToken.reverse()
                assert sc.refreshToken == newRefreshToken.reverse()
                assert sc.secret == newSecret.reverse()
                assert sc.userId == TESTID
                return sc
            }
        })
        repository.updateConnection(
                new FakeFacebookConnection(
                        new ConnectionData(
                                FACEBOOK,
                                FACEBOOK2SC.providerUserId,
                                newDisplayName,
                                newProfile,
                                newImage,
                                newAccessToken,
                                newSecret,
                                newRefreshToken,
                                newExpire)))
        verify(socialConnectionRepository).save(isA(StringSocialConnection.class))
    }

    private
    static void compareConnectionToSocialConnection(
            final Connection<?> connection, final StringSocialConnection socialConnection) {
        assert connection instanceof FakeConnection
        assert connection.displayName == socialConnection.displayName
        assert connection.profileUrl == socialConnection.profileUrl
        assert connection.key.providerId == socialConnection.providerId
        assert connection.key.providerUserId == socialConnection.providerUserId
        assert connection.imageUrl == socialConnection.imageUrl
        assert connection.accessToken == socialConnection.accessToken?.reverse()
        assert connection.refreshToken == socialConnection.refreshToken?.reverse()
        assert connection.secret == socialConnection.secret?.reverse()
        assert connection.expireTime == socialConnection.expireTime
    }

}

