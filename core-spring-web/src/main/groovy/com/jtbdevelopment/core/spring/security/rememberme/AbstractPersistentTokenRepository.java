package com.jtbdevelopment.core.spring.security.rememberme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.io.Serializable;
import java.util.Date;

/**
 * Date: 12/26/14
 * Time: 9:08 AM
 */
public abstract class AbstractPersistentTokenRepository<ID extends Serializable, T extends AbstractRememberMeToken<ID>> implements PersistentTokenRepository {
    private AbstractRememberMeTokenRepository<ID, T> rememberMeTokenRepository;

    @Autowired
    public AbstractPersistentTokenRepository(AbstractRememberMeTokenRepository<ID, T> rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository;
    }

    protected abstract T newToken(final PersistentRememberMeToken source);

    protected abstract T newToken(final ID id, final String username, final String series, final String tokenValue, final Date date);

    public void createNewToken(final PersistentRememberMeToken token) {
        rememberMeTokenRepository.save(newToken(token));
    }

    public void updateToken(final String series, final String tokenValue, final Date lastUsed) {
        T token = rememberMeTokenRepository.findBySeries(series);
        if (token != null) {
            T newToken = newToken(token.getId(), token.getUsername(), token.getSeries(), tokenValue, lastUsed);
            rememberMeTokenRepository.save(newToken);
        }

    }

    public T getTokenForSeries(final String seriesId) {
        return rememberMeTokenRepository.findBySeries(seriesId);
    }

    public void removeUserTokens(final String username) {
        rememberMeTokenRepository.findByUsername(username).forEach(token -> rememberMeTokenRepository.delete(token));
    }

    public void setRememberMeTokenRepository(AbstractRememberMeTokenRepository<ID, T> rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository;
    }

}
