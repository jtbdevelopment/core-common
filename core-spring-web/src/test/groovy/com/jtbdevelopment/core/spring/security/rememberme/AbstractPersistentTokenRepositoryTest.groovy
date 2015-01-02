package com.jtbdevelopment.core.spring.security.rememberme

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken

/**
 * Date: 1/2/15
 * Time: 6:42 AM
 *
 * Loosely based on spring's own JdbcTokenRepositoryImplTests
 *
 */
class AbstractPersistentTokenRepositoryTest extends GroovyTestCase {
    private static class TestPersistentToken extends AbstractRememberMeToken<String> {
        String id

        TestPersistentToken(
                final String username,
                final String series, final String tokenValue, final Date date, final String id) {
            super(username, series, tokenValue, date)
            this.id = id
        }
    }

    private
    static class TestPersistentTokenRepository extends AbstractPersistentTokenRepository<String, TestPersistentToken> {
        @Override
        TestPersistentToken newToken(final PersistentRememberMeToken source) {
            return newToken(null, source.username, source.series, source.tokenValue, source.date)
        }

        @Override
        TestPersistentToken newToken(
                final String s, final String username, final String series, final String tokenValue, final Date date) {
            return new TestPersistentToken(username, series, tokenValue, date, s)
        }
    }

    protected AbstractPersistentTokenRepository repository;

    @Override
    protected void setUp() throws Exception {
        repository = new TestPersistentTokenRepository()
    }

    public void testCreateNewTokenInsertsCorrectData() {
        Date currentDate = new Date();
        PersistentRememberMeToken token = new PersistentRememberMeToken("joeuser", "joesseries", "atoken", currentDate);

        boolean saveCalled = false
        repository.rememberMeTokenRepository = [
                save: {
                    AbstractRememberMeToken it ->
                        assert it.id == null
                        assert it.username == "joeuser"
                        assert it.series == "joesseries"
                        assert it.date == currentDate
                        assert it.tokenValue == "atoken"
                        assertFalse saveCalled
                        saveCalled = true
                }
        ] as AbstractRememberMeTokenRepository
        repository.createNewToken(token)
        assert saveCalled
    }

    public void testRetrievingTokenReturnsCorrectData() {

        def series = "joesseries"
        AbstractRememberMeToken token = repository.newToken(new PersistentRememberMeToken("joeuser", series, "atoken", new Date()))
        repository.rememberMeTokenRepository = [
                findBySeries: {
                    String s ->
                        assert s == series
                        return token
                }
        ] as AbstractRememberMeTokenRepository
        PersistentRememberMeToken loaded = repository.getTokenForSeries(series);

        assert loaded.is(token)
    }

    public void testRemovingUserTokensDeletesData() {
        AbstractRememberMeToken token1 = repository.newToken(new PersistentRememberMeToken("joeuser", "series1", "atoken1", new Date()))
        AbstractRememberMeToken token2 = repository.newToken(new PersistentRememberMeToken("joeuser", "series2", "atoken2", new Date()))
        boolean t1Deleted = false, t2Deleted = false
        repository.rememberMeTokenRepository = [
                findByUsername: {
                    String u ->
                        assert u == "joeuser"
                        return [token1, token2]
                },
                delete        : {
                    Iterable<AbstractRememberMeToken> it ->
                        it.each {
                            AbstractRememberMeToken token ->
                                assert token.is(token1) || token.is(token2)
                                if (token.is(token1)) {
                                    assertFalse t1Deleted
                                    t1Deleted = true
                                }
                                if (token.is(token2)) {
                                    assertFalse t2Deleted
                                    t2Deleted = true
                                }
                        }
                }

        ] as AbstractRememberMeTokenRepository
        repository.removeUserTokens("joeuser");
        assert t1Deleted
        assert t2Deleted
    }

    public void testUpdatingTokenModifiesTokenValueAndLastUsed() {
        AbstractRememberMeToken initialToken = repository.newToken(new PersistentRememberMeToken("joeuser", "joesseries", "atoken", new Date()))

        def newDate = new Date()
        def newToken = "newtoken"
        boolean saveCalled = false
        repository.rememberMeTokenRepository = [
                findBySeries: {
                    String s ->
                        assert s == "joesseries"
                        return initialToken
                },
                save        : {
                    AbstractRememberMeToken it ->
                        assert it.id == initialToken.id
                        assert it.username == "joeuser"
                        assert it.series == "joesseries"
                        assert it.date == newDate
                        assert it.tokenValue == newToken
                        assertFalse saveCalled
                        saveCalled = true
                }
        ] as AbstractRememberMeTokenRepository
        repository.updateToken("joesseries", newToken, newDate);
        assert saveCalled
    }

    public void testUpdatingTokenWithNoSeries() {
        def newDate = new Date()
        def newToken = "newtoken"
        boolean saveCalled = false
        repository.rememberMeTokenRepository = [
                findBySeries: {
                    String s ->
                        assert s == "joesseries"
                        return null
                }
        ] as AbstractRememberMeTokenRepository
        repository.updateToken("joesseries", newToken, newDate);
        assertFalse saveCalled
    }
}
