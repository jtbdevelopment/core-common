package com.jtbdevelopment.core.spring.social.dao

import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.Sort
import org.springframework.social.connect.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

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

    private StringConnectionRepository repository = new StringConnectionRepository(TESTID)

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        StringConnectionRepository.connectionFactoryLocator = connectionFactoryLocator
        StringConnectionRepository.encryptor = textEncryptor
        StringConnectionRepository.providerConnectionFactoryMap = providers
    }

    void testSortDefinition() {
        assert StringConnectionRepository.SORT.toString() == 'providerId: ASC,creationTime: ASC'
    }

    void testFindAllConnectionsWithValidProviders() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserId: {
                    String id, Sort s ->
                        assert s.is(AbstractConnectionRepository.SORT)
                        assert id == TESTID
                        return [FACEBOOK1SC, FACEBOOK2SC, TWITTER1SC]
                }
        ] as AbstractSocialConnectionRepository
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
        StringConnectionRepository.socialConnectionRepository = [
                findByUserId: {
                    String id, Sort s ->
                        assert s.is(AbstractConnectionRepository.SORT)
                        assert id == TESTID
                        return []
                }
        ] as AbstractSocialConnectionRepository
        MultiValueMap<String, Connection<?>> r = repository.findAllConnections()
        assert r.containsKey(FACEBOOK)
        assert r[FACEBOOK].size() == 0
        assert r.containsKey(TWITTER)
        assert r[TWITTER].size() == 0
    }

    void testFindAllConnectionsWithInvalidProviders() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserId: {
                    String id, Sort s ->
                        assert s.is(AbstractConnectionRepository.SORT)
                        assert id == TESTID
                        return [FACEBOOK1SC, FACEBOOK2SC, TWITTER1SC, NEWCO1SC]
                }
        ] as AbstractSocialConnectionRepository
        shouldFail(IllegalArgumentException.class) {
            repository.findAllConnections()
        }
    }

    void testFindConnectionsByProviderId() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderId: {
                    String id, String providerId, Sort s ->
                        assert s.is(AbstractConnectionRepository.SORT)
                        assert providerId == FACEBOOK
                        assert id == TESTID
                        return [FACEBOOK2SC, FACEBOOK1SC]
                }
        ] as AbstractSocialConnectionRepository
        List<Connection<?>> connections = repository.findConnections(FACEBOOK)
        assert connections.size() == 2
        compareConnectionToSocialConnection(connections[0], FACEBOOK2SC)
        compareConnectionToSocialConnection(connections[1], FACEBOOK1SC)
    }

    void testFindConnectionsByProviderIdWithEmptyResults() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderId: {
                    String id, String providerId, Sort s ->
                        assert s.is(AbstractConnectionRepository.SORT)
                        assert providerId == NEWCO
                        assert id == TESTID
                        return []
                }
        ] as AbstractSocialConnectionRepository
        List<Connection<?>> connections = repository.findConnections(NEWCO)
        assert connections.size() == 0
    }

    void testFindConnectionsByProviderApi() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderId: {
                    String id, String providerId, Sort s ->
                        assert s.is(AbstractConnectionRepository.SORT)
                        assert providerId == FACEBOOK
                        assert id == TESTID
                        return [FACEBOOK2SC, FACEBOOK1SC]
                }
        ] as AbstractSocialConnectionRepository
        List<Connection<?>> connections = repository.findConnections(FakeFacebookApi.class)
        assert connections.size() == 2
        compareConnectionToSocialConnection(connections[0], FACEBOOK2SC)
        compareConnectionToSocialConnection(connections[1], FACEBOOK1SC)
    }

    void testFindConnectionsByProviderApiWithEmptyResults() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderId: {
                    String id, String providerId, Sort s ->
                        assert s.is(AbstractConnectionRepository.SORT)
                        assert providerId == TWITTER
                        assert id == TESTID
                        return []
                }
        ] as AbstractSocialConnectionRepository
        List<Connection<?>> connections = repository.findConnections(FakeTwitterApi.class)
        assert connections.size() == 0
    }

    void testFindConnectionsToProviderUserIds() {
        def TPIDS = ["DONTEXIST", TWITTER1SC.providerUserId]
        def FBPIDS = [FACEBOOK1SC.providerUserId, FACEBOOK2SC.providerUserId]
        MultiValueMap<String, String> input = new LinkedMultiValueMap<>();
        input.put(FACEBOOK, FBPIDS)
        input.put(TWITTER, TPIDS)
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderIdAndProviderUserIdIn: {
                    String uid, String p, Collection<String> pids, Sort sort ->
                        assert p == FACEBOOK || p == TWITTER
                        assert sort.is(StringConnectionRepository.SORT)
                        assert uid == TESTID
                        switch (p) {
                            case FACEBOOK:
                                assert pids == FBPIDS
                                return [FACEBOOK2SC, FACEBOOK1SC, FACEBOOK2SCDUPE]  // Tests order of results from DB only important with dupe provider user ids
                                break;
                            case TWITTER:
                                assert pids == TPIDS
                                return [TWITTER1SC, NEWCO1SC] // Tests only one id coming back, plus an irrelevant one
                                break;
                        }
                }
        ] as AbstractSocialConnectionRepository
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
        MultiValueMap<String, String> input = new LinkedMultiValueMap<>();
        MultiValueMap<String, Connection<?>> result = repository.findConnectionsToUsers(input)
        assert result.isEmpty()
    }

    void testFindByConnectionKey() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderIdAndProviderUserId: {
                    String uid, String p, String pid ->
                        assert uid == TESTID
                        assert p == TWITTER
                        assert pid == TWITTER1SC.providerUserId
                        return TWITTER1SC
                }
        ] as AbstractSocialConnectionRepository
        compareConnectionToSocialConnection(repository.getConnection(new ConnectionKey(TWITTER, TWITTER1SC.providerUserId)), TWITTER1SC)
    }

    void testFindByConnectionKeyNotFound() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderIdAndProviderUserId: {
                    String uid, String p, String pid ->
                        assert uid == TESTID
                        assert p == TWITTER
                        assert pid == TWITTER1SC.providerUserId
                        return null
                }
        ] as AbstractSocialConnectionRepository
        shouldFail(NoSuchConnectionException.class) {
            repository.getConnection(new ConnectionKey(TWITTER, TWITTER1SC.providerUserId))
        }
    }

    void testFindByAPIAndPID() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderIdAndProviderUserId: {
                    String uid, String p, String pid ->
                        assert uid == TESTID
                        assert p == FACEBOOK
                        assert pid == FACEBOOK2SC.providerUserId
                        return FACEBOOK2SC
                }
        ] as AbstractSocialConnectionRepository
        compareConnectionToSocialConnection(repository.getConnection(FakeFacebookApi.class, FACEBOOK2SC.providerUserId), FACEBOOK2SC)
    }

    void testFindByAPIAndPIDNotFound() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderIdAndProviderUserId: {
                    String uid, String p, String pid ->
                        assert uid == TESTID
                        assert p == FACEBOOK
                        assert pid == FACEBOOK2SC.providerUserId
                        return null
                }
        ] as AbstractSocialConnectionRepository
        shouldFail(NoSuchConnectionException.class) {
            repository.getConnection(FakeFacebookApi.class, FACEBOOK2SC.providerUserId)
        }
    }

    void testGetPrimaryConnection() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderId: {
                    String uid, String p, Sort s ->
                        assert uid == TESTID
                        assert p == FACEBOOK
                        assert s.is(StringConnectionRepository.SORT)
                        return [FACEBOOK2SC, FACEBOOK1SC]
                }
        ] as AbstractSocialConnectionRepository
        compareConnectionToSocialConnection(repository.getPrimaryConnection(FakeFacebookApi.class), FACEBOOK2SC)
    }

    void testGetPrimaryConnectionNoResults() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderId: {
                    String uid, String p, Sort s ->
                        assert uid == TESTID
                        assert p == FACEBOOK
                        assert s.is(StringConnectionRepository.SORT)
                        return []
                }
        ] as AbstractSocialConnectionRepository
        shouldFail(NotConnectedException.class) {
            repository.getPrimaryConnection(FakeFacebookApi.class)
        }
    }

    void testFindPrimaryConnection() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderId: {
                    String uid, String p, Sort s ->
                        assert uid == TESTID
                        assert p == FACEBOOK
                        assert s.is(StringConnectionRepository.SORT)
                        return [FACEBOOK2SC, FACEBOOK1SC]
                }
        ] as AbstractSocialConnectionRepository
        compareConnectionToSocialConnection(repository.findPrimaryConnection(FakeFacebookApi.class), FACEBOOK2SC)
    }

    void testFindPrimaryConnectionNoResults() {
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderId: {
                    String uid, String p, Sort s ->
                        assert uid == TESTID
                        assert p == FACEBOOK
                        assert s.is(StringConnectionRepository.SORT)
                        return []
                }
        ] as AbstractSocialConnectionRepository
        assertNull repository.findPrimaryConnection(FakeFacebookApi.class)
    }

    void testRemoveConnections() {
        boolean deleteCalled = false
        StringConnectionRepository.socialConnectionRepository = [
                deleteByUserIdAndProviderId: {
                    String uid, String p ->
                        assert uid == TESTID
                        assert p == TWITTER
                        deleteCalled = true
                        return 1L
                }
        ] as AbstractSocialConnectionRepository
        repository.removeConnections(TWITTER)
        assert deleteCalled
    }

    void testRemoveConnection() {
        boolean deleteCalled = false
        StringConnectionRepository.socialConnectionRepository = [
                deleteByUserIdAndProviderIdAndProviderUserId: {
                    String uid, String p, String pid ->
                        assert uid == TESTID
                        assert p == TWITTER
                        assert pid == TWITTER1SC.providerUserId
                        deleteCalled = true
                        return 1L
                }
        ] as AbstractSocialConnectionRepository
        repository.removeConnection(new ConnectionKey(TWITTER, TWITTER1SC.providerUserId))
        assert deleteCalled
    }

    void testAddConnection() {
        boolean saveCalled = false
        StringConnectionRepository.socialConnectionRepository = [
                save: {
                    SocialConnection sc ->
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
                        saveCalled = true
                        return sc
                }
        ] as AbstractSocialConnectionRepository
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
        assert saveCalled
    }

    void testAddConnectionDuplicate() {
        StringConnectionRepository.socialConnectionRepository = [
                save: {
                    SocialConnection sc ->
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
                        throw new DuplicateKeyException('dupe')
                }
        ] as AbstractSocialConnectionRepository
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
        StringConnectionRepository.socialConnectionRepository = [
                findByUserIdAndProviderIdAndProviderUserId: {
                    String uid, String p, String pid ->
                        assert uid == TESTID
                        assert p == FACEBOOK
                        assert pid == FACEBOOK2SC.providerUserId
                        return FACEBOOK2SC
                },
                save                                      : {
                    SocialConnection sc ->
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
                        saveCalled = true
                        return sc
                }
        ] as AbstractSocialConnectionRepository
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
        assert saveCalled
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
    }

}

