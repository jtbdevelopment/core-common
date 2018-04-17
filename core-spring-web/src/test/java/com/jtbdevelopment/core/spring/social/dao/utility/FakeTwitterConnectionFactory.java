package com.jtbdevelopment.core.spring.social.dao.utility;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;

/**
 * Date: 1/4/2015 Time: 7:53 AM
 */
public class FakeTwitterConnectionFactory extends FakeConnectionFactory<FakeTwitterApi> {

  public FakeTwitterConnectionFactory() {
    super(FakeTwitterApi.TWITTER);
  }

  @Override
  public Connection<FakeTwitterApi> createConnection(final ConnectionData data) {
    return new FakeTwitterConnection(data);
  }

}
