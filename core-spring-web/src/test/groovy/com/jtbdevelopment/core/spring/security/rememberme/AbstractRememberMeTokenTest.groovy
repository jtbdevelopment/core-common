package com.jtbdevelopment.core.spring.security.rememberme

/**
 * Date: 1/2/15
 * Time: 6:36 AM
 */
class AbstractRememberMeTokenTest extends GroovyTestCase {
    private static class StringRememberMeToken extends AbstractRememberMeToken<String> {
        String id;

        StringRememberMeToken(
                final String username, final String series, final String tokenValue, final Date date, final String id) {
            super(username, series, tokenValue, date)
            this.id = id
        }
    }

    public void testConstructor() {
        def user = "user"

        def series = "series"

        def tokenValue = "token"

        def date = new Date()

        def id = "id"
        def token = new StringRememberMeToken(
                user,
                series,
                tokenValue,
                date,
                id
        )

        assert token.id == id
        assert token.date == date
        assert token.series == series
        assert token.tokenValue == tokenValue
        assert token.username == user
    }
}
