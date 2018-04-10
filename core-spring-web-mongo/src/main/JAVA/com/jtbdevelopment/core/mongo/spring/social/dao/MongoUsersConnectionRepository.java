package com.jtbdevelopment.core.mongo.spring.social.dao;

import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository;
import groovy.transform.CompileStatic;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 12/16/14 Time: 12:59 PM
 */
@CompileStatic
@Component
public class MongoUsersConnectionRepository extends AbstractUsersConnectionRepository {

  @Override
  public ConnectionRepository createConnectionRepository(final String userId) {
    if (StringUtils.isEmpty(userId)) {
      throw new IllegalArgumentException("userId cannot be null");
    }

    return new MongoConnectionRepository(userId);
  }

}
