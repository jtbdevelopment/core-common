package com.jtbdevelopment.core.mongo.spring.social.dao;

import com.jtbdevelopment.core.spring.social.dao.AbstractConnectionRepository;
import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnection;

/**
 * Date: 12/16/14 Time: 1:17 PM
 */
public class MongoConnectionRepository extends AbstractConnectionRepository {

  public MongoConnectionRepository(final String userId) {
    super(userId);
  }

  @Override
  public AbstractSocialConnection createSocialConnection() {
    return new MongoSocialConnection();
  }

}
