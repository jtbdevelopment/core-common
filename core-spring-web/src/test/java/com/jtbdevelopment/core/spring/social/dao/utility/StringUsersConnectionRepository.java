package com.jtbdevelopment.core.spring.social.dao.utility;

import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnectionRepository;
import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;

/**
 * Date: 4/16/18 Time: 8:52 PM
 */
public class StringUsersConnectionRepository extends
    AbstractUsersConnectionRepository<String, StringSocialConnection> {

  private ConnectionFactoryLocator connectionFactoryLocator;
  private TextEncryptor textEncryptor;

  public StringUsersConnectionRepository(ConnectionSignUp connectionSignUp,
      AbstractSocialConnectionRepository socialConnectionRepository,
      TextEncryptor textEncryptor,
      ConnectionFactoryLocator connectionFactoryLocator) {
    super(connectionSignUp, socialConnectionRepository);
    this.connectionFactoryLocator = connectionFactoryLocator;
    this.textEncryptor = textEncryptor;
  }

  @Override
  public ConnectionRepository createConnectionRepository(final String userId) {
    return new StringConnectionRepository(socialConnectionRepository,
        connectionFactoryLocator,
        textEncryptor, userId);
  }
}
