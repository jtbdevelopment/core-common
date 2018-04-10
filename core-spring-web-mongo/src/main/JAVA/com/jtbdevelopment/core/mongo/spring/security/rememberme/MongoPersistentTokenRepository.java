package com.jtbdevelopment.core.mongo.spring.security.rememberme;

import com.jtbdevelopment.core.spring.security.rememberme.AbstractPersistentTokenRepository;
import com.jtbdevelopment.core.spring.security.rememberme.AbstractRememberMeTokenRepository;
import groovy.transform.CompileStatic;
import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Component;

/**
 * Date: 12/26/14 Time: 9:08 AM
 */
@CompileStatic
@Component
public class MongoPersistentTokenRepository extends
    AbstractPersistentTokenRepository<ObjectId, MongoRememberMeToken> {

  public MongoPersistentTokenRepository(
      AbstractRememberMeTokenRepository<ObjectId, MongoRememberMeToken> rememberMeTokenRepository) {
    super(rememberMeTokenRepository);
  }

  @Override
  protected MongoRememberMeToken newToken(final PersistentRememberMeToken source) {
    return new MongoRememberMeToken(source);
  }

  @Override
  protected MongoRememberMeToken newToken(final ObjectId objectId, final String username,
      final String series, final String tokenValue, final Date date) {
    return new MongoRememberMeToken(username, series, tokenValue, date, objectId);
  }

}
