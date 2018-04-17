package com.jtbdevelopment.core.spring.social.dao.utility;

import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.AbstractConnection;

/**
 * Date: 1/4/2015 Time: 7:50 AM
 */
public abstract class FakeConnection<A> extends AbstractConnection<A> {

  private final Long expireTime;
  private final String accessToken;
  private final String refreshToken;
  private final String secret;
  private final String providerUserId;
  private final String providerId;

  public FakeConnection(final ConnectionData data) {
    super(data, null);
    this.expireTime = data.getExpireTime();
    this.accessToken = data.getAccessToken();
    this.refreshToken = data.getRefreshToken();
    this.secret = data.getSecret();
    this.providerUserId = data.getProviderUserId();
    this.providerId = data.getProviderId();
  }

  @Override
  public ConnectionData createData() {
    return new ConnectionData(providerId, providerUserId, getDisplayName(), getProfileUrl(),
        getImageUrl(), accessToken, secret, refreshToken, expireTime);
  }

  public final Long getExpireTime() {
    return expireTime;
  }

  public final String getAccessToken() {
    return accessToken;
  }

  public final String getRefreshToken() {
    return refreshToken;
  }

  public final String getSecret() {
    return secret;
  }

  public final String getProviderUserId() {
    return providerUserId;
  }

  public final String getProviderId() {
    return providerId;
  }
}
