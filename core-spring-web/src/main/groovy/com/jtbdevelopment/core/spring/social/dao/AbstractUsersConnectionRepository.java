package com.jtbdevelopment.core.spring.social.dao;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * Date: 12/16/14 Time: 12:59 PM
 */
public abstract class AbstractUsersConnectionRepository implements UsersConnectionRepository {

  private final AbstractSocialConnectionRepository<? extends Serializable, ? extends SocialConnection> socialConnectionRepository;
  private final ConnectionSignUp connectionSignUp;

  @Autowired
  public AbstractUsersConnectionRepository(
      final ConnectionSignUp connectionSignUp,
      final AbstractSocialConnectionRepository<? extends Serializable, ? extends SocialConnection> socialConnectionRepository,
      final ConnectionFactoryLocator connectionFactoryLocator,
      final TextEncryptor textEncryptor) {
    this.socialConnectionRepository = socialConnectionRepository;
    this.connectionSignUp = connectionSignUp;
    AbstractConnectionRepository.setConnectionFactoryLocator(connectionFactoryLocator);
    AbstractConnectionRepository.setEncryptor(textEncryptor);
    AbstractConnectionRepository.setSocialConnectionRepository(socialConnectionRepository);
    AbstractConnectionRepository.setProviderConnectionFactoryMap(
        connectionFactoryLocator.registeredProviderIds()
            .stream()
            .collect(Collectors.toMap(x -> x,
                connectionFactoryLocator::getConnectionFactory)));
  }

  @Override
  public List<String> findUserIdsWithConnection(final Connection<?> connection) {
    ConnectionKey key = connection.getKey();
    List<? extends SocialConnection> connections =
        socialConnectionRepository.findByProviderIdAndProviderUserId(
            key.getProviderId(),
            key.getProviderUserId());
    if (connections.size() == 0) {
      String newUserId = connectionSignUp.execute(connection);
      if (newUserId != null) {
        createConnectionRepository(newUserId).addConnection(connection);
        return Collections.singletonList(newUserId);
      }

    }
    return connections.stream().map(SocialConnection::getUserId).collect(Collectors.toList());
  }

  @Override
  public Set<String> findUserIdsConnectedTo(final String providerId,
      final Set<String> providerUserIds) {
    List<? extends SocialConnection> connections =
        socialConnectionRepository.findByProviderIdAndProviderUserIdIn(providerId, providerUserIds);
    return connections.stream().map(SocialConnection::getUserId).collect(Collectors.toSet());
  }

}
