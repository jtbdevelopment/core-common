package com.jtbdevelopment.core.spring.security.rememberme

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken

/**
 * Date: 12/30/2014
 * Time: 3:54 PM
 */
abstract class AbstractRememberMeToken<ID extends Serializable> extends PersistentRememberMeToken {
    abstract ID getId()

    abstract void setId(final ID id)

    AbstractRememberMeToken(final String username, final String series, final String tokenValue, final Date date) {
        super(username, series, tokenValue, date)
    }
}
