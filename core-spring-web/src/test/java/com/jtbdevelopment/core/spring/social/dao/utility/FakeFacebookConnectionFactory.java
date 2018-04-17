package com.jtbdevelopment.core.spring.social.dao.utility;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;

/**
 * Date: 1/4/2015 Time: 7:53 AM
 */
public class FakeFacebookConnectionFactory extends FakeConnectionFactory<FakeFacebookApi> {

  public FakeFacebookConnectionFactory() {
    super(FakeFacebookApi.FACEBOOK);
  }

  @Override
  public Connection<FakeFacebookApi> createConnection(final ConnectionData data) {
    return new FakeFacebookConnection(data);
  }

}
