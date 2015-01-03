package com.jtbdevelopment.core.spring.social.dao

import org.springframework.data.domain.Sort
import org.springframework.social.connect.Connection
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
    static FACEBOOK1SC = new StringSocialConnection(id: 'L1', providerId: FACEBOOK, providerUserId: 'F1', profileUrl: 'F1P', imageUrl: 'F1I', accessToken: 'F1A', secret: 'F1S', expireTime: 1, displayName: 'F1DN')
    private
    static FACEBOOK2SC = new StringSocialConnection(id: 'L1', providerId: FACEBOOK, providerUserId: 'F2', profileUrl: 'F2P', imageUrl: 'F2I', accessToken: 'F2A', secret: 'F2S', expireTime: 2, displayName: 'F2DN')
    private
    static TWITTER1SC = new StringSocialConnection(id: 'L1', providerId: TWITTER, providerUserId: 'T1', profileUrl: 'T1P', imageUrl: 'T1I', accessToken: 'T1A', refreshToken: 'T1RT', expireTime: 1, displayName: 'T1DN')
    private
    static NEWCO1SC = new StringSocialConnection(id: 'L1', providerId: NEWCO, providerUserId: 'N1', profileUrl: 'N1P', imageUrl: 'N1I', expireTime: 2, displayName: 'N1DN')

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
        assert r.containsKey(TWITTER)
        assert r[TWITTER].size() == 1
    }

    void testFindAllConnectionsWithEmptyProviders() {
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
}

