package com.jtbdevelopment.core.mongo.spring.social.dao;

import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository;
import groovy.transform.CompileStatic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 12/16/14 Time: 12:59 PM
 */
@CompileStatic
@Component
public class MongoUsersConnectionRepository extends
    AbstractUsersConnectionRepository<ObjectId, MongoSocialConnection> {

  private final TextEncryptor textEncryptor;
  private final ConnectionFactoryLocator connectionFactoryLocator;

  public MongoUsersConnectionRepository(
      @Autowired(required = false) final ConnectionSignUp connectionSignUp,
      final MongoSocialConnectionRepository socialConnectionRepository,
      final ConnectionFactoryLocator connectionFactoryLocator,
      final TextEncryptor textEncryptor) {
    super(connectionSignUp, socialConnectionRepository);
    this.textEncryptor = textEncryptor;
    this.connectionFactoryLocator = connectionFactoryLocator;
  }

  @Override
  public ConnectionRepository createConnectionRepository(final String userId) {
    if (StringUtils.isEmpty(userId)) {
      throw new IllegalArgumentException("userId cannot be null");
    }

    return new MongoConnectionRepository(
        (MongoSocialConnectionRepository) socialConnectionRepository,
        connectionFactoryLocator,
        textEncryptor,
        userId);
  }

}
