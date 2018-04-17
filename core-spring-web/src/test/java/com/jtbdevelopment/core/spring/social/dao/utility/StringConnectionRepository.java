package com.jtbdevelopment.core.spring.social.dao.utility;

import com.jtbdevelopment.core.spring.social.dao.AbstractConnectionRepository;
import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnectionRepository;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactoryLocator;

/**
 * Date: 4/16/18 Time: 8:42 PM
 */
public class StringConnectionRepository extends
    AbstractConnectionRepository<String, StringSocialConnection> {

  public StringConnectionRepository(
      AbstractSocialConnectionRepository<String, StringSocialConnection> socialConnectionRepository,
      ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor encryptor,
      String userId) {
    super(socialConnectionRepository, connectionFactoryLocator, encryptor, userId);
  }

  @Override
  public StringSocialConnection createSocialConnection() {
    return new StringSocialConnection();
  }

}
