package com.jtbdevelopment.core.mongo.spring.social.dao;

import com.jtbdevelopment.core.spring.social.dao.AbstractConnectionRepository;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactoryLocator;

/**
 * Date: 12/16/14 Time: 1:17 PM
 */
public class MongoConnectionRepository extends
    AbstractConnectionRepository<ObjectId, MongoSocialConnection> {

  public MongoConnectionRepository(
      final MongoSocialConnectionRepository socialConnectionRepository,
      final ConnectionFactoryLocator connectionFactoryLocator,
      final TextEncryptor encryptor,
      final String userId) {
    super(socialConnectionRepository, connectionFactoryLocator, encryptor, userId);
  }

  @Override
  public MongoSocialConnection createSocialConnection() {
    return new MongoSocialConnection();
  }

}
