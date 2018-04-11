package com.jtbdevelopment.core.spring.social.dao;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Date: 12/30/2014 Time: 5:22 PM
 */
public interface SocialConnection<ID extends Serializable> {

  ID getId();

  void setId(final ID id);

  String getAccessToken();

  void setAccessToken(final String accessToken);

  ZonedDateTime getCreated();

  void setCreated(final ZonedDateTime created);

  String getDisplayName();

  void setDisplayName(final String displayName);

  Long getExpireTime();

  void setExpireTime(final Long expireTime);

  String getImageUrl();

  void setImageUrl(final String imageUrl);

  String getProfileUrl();

  void setProfileUrl(final String profileUrl);

  String getProviderId();

  void setProviderId(final String providerId);

  String getProviderUserId();

  void setProviderUserId(final String providerUserId);

  String getRefreshToken();

  void setRefreshToken(final String refreshToken);

  String getSecret();

  void setSecret(final String secret);

  String getUserId();

  void setUserId(final String userId);
}
