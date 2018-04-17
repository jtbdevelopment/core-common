package com.jtbdevelopment.core.spring.social.dao.utility;

import org.springframework.social.connect.ConnectionData;

/**
 * Date: 1/4/2015 Time: 7:51 AM
 */
public class FakeTwitterConnection extends FakeConnection<FakeTwitterApi> {

  public FakeTwitterConnection(final ConnectionData data) {
    super(data);
  }

  @Override
  public FakeTwitterApi getApi() {
    return null;
  }

}
