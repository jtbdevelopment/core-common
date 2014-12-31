package com.jtbdevelopment.core.spring.security.rememberme

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository

/**
 * Date: 12/26/14
 * Time: 9:08 AM
 */
@CompileStatic
abstract class AbstractPersistentTokenRepository<ID extends Serializable, T extends AbstractRememberMeToken<ID>> implements PersistentTokenRepository {
    @Autowired
    AbstractRememberMeTokenRepository<ID, T> rememberMeTokenRepository

    abstract T newToken(final PersistentRememberMeToken source)

    abstract T newToken(
            final ID id, final String username, final String series, final String tokenValue, final Date date)

    @Override
    void createNewToken(final PersistentRememberMeToken token) {
        rememberMeTokenRepository.save(newToken(token))
    }

    @Override
    void updateToken(final String series, final String tokenValue, final Date lastUsed) {
        T token = rememberMeTokenRepository.findBySeries(series)
        if (token) {
            T newToken = newToken(token.id, token.username, token.series, tokenValue, lastUsed)
            rememberMeTokenRepository.save(newToken)
        }
    }

    @Override
    T getTokenForSeries(final String seriesId) {
        return rememberMeTokenRepository.findBySeries(seriesId)
    }

    @Override
    void removeUserTokens(final String username) {
        rememberMeTokenRepository.delete(rememberMeTokenRepository.findByUsername(username))
    }
}
