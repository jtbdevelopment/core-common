package com.jtbdevelopment.core.spring.security.rememberme;

import java.io.Serializable;
import java.util.Date;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * Date: 12/30/2014 Time: 3:54 PM
 */
public abstract class AbstractRememberMeToken<ID extends Serializable> extends
    PersistentRememberMeToken {

  public AbstractRememberMeToken(final String username, final String series,
      final String tokenValue, final Date date) {
    super(username, series, tokenValue, date);
    }

  public abstract ID getId();

  public abstract void setId(final ID id);
}
