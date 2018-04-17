package com.jtbdevelopment.core.spring.social.dao;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Date: 12/16/14 Time: 1:17 PM
 */
public abstract class AbstractConnectionRepository<ID extends Serializable, SC extends AbstractSocialConnection<ID>>
    implements ConnectionRepository {

  protected static final Sort SORT_PID_CREATED = Sort.by(new Order(Direction.ASC, "providerId"),
      new Order(Direction.ASC, "created"));
  protected static final Sort SORT_CREATED = Sort.by(new Order(Direction.ASC, "created"));

  private static final Map<String, ConnectionFactory<?>> providerConnectionFactoryMap = new LinkedHashMap<>();
  private static final Logger logger = LoggerFactory.getLogger(AbstractConnectionRepository.class);
  private final AbstractSocialConnectionRepository<ID, SC> socialConnectionRepository;
  private final ConnectionFactoryLocator connectionFactoryLocator;
  private final TextEncryptor encryptor;
  private final String userId;

  protected AbstractConnectionRepository(
      final AbstractSocialConnectionRepository<ID, SC> socialConnectionRepository,
      final ConnectionFactoryLocator connectionFactoryLocator,
      final TextEncryptor encryptor,
      final String userId) {
    this.socialConnectionRepository = socialConnectionRepository;
    this.connectionFactoryLocator = connectionFactoryLocator;
    this.encryptor = encryptor;
    this.userId = userId;
    if (providerConnectionFactoryMap.isEmpty() && connectionFactoryLocator != null) {
      synchronized (providerConnectionFactoryMap) {
        if (providerConnectionFactoryMap.isEmpty()) {
          Set<String> strings = connectionFactoryLocator.registeredProviderIds();
          providerConnectionFactoryMap.putAll(
              strings
                  .stream()
                  .collect(Collectors.toMap(x -> x,
                      connectionFactoryLocator::getConnectionFactory)));

        }
      }
    }
  }

  @Override
  public MultiValueMap<String, Connection<?>> findAllConnections() {
    MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
    providerConnectionFactoryMap.keySet().forEach(provider ->
        connections.addAll(provider, Collections.emptyList()));

    List<? extends SocialConnection> socialConnections =
        socialConnectionRepository.findByUserId(userId, SORT_PID_CREATED);
    socialConnections.forEach(sc ->
        connections.add(sc.getProviderId(), mapSocialConnectionToConnection(sc)));
    return connections;
  }

  @Override
  public List<Connection<?>> findConnections(final String providerId) {
    List<? extends SocialConnection> connections = socialConnectionRepository
        .findByUserIdAndProviderId(userId, providerId, SORT_CREATED);
    return connections.stream()
        .map(this::mapSocialConnectionToConnection)
        .collect(Collectors.toList());
  }

  @Override
  public <A> List<Connection<A>> findConnections(final Class<A> apiType) {
    List<?> connections = findConnections(getProviderId(apiType));
    return (List<Connection<A>>) connections;
  }

  @Override
  public MultiValueMap<String, Connection<?>> findConnectionsToUsers(
      final MultiValueMap<String, String> providerIdProviderUserIdList) {
    MultiValueMap<String, Connection<?>> connectionsByProvider = new LinkedMultiValueMap<>();
    if (providerIdProviderUserIdList != null) {
      providerIdProviderUserIdList.keySet().forEach(provider ->
          connectionsByProvider.addAll(provider, Collections.emptyList()));

      providerIdProviderUserIdList.forEach((providerId, providerUserIds) -> {
        Map<String, Connection<?>> connections =
            socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserIdIn(
                userId,
                providerId,
                providerUserIds,
                SORT_PID_CREATED)
                .stream()
                .filter(sc -> providerConnectionFactoryMap.containsKey(sc.getProviderId()))
                .collect(
                    Collectors.toMap(
                        AbstractSocialConnection::getProviderUserId,
                        this::mapSocialConnectionToConnection,
                        (a, b) -> b
                    )
                );

        providerUserIds.forEach(providerUserId -> connectionsByProvider
            .add(providerId, connections.get(providerUserId)));
      });
    }
    return connectionsByProvider;
  }

  @Override
  public Connection<?> getConnection(final ConnectionKey connectionKey) {
    SocialConnection connection = socialConnectionRepository
        .findByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.getProviderId(),
            connectionKey.getProviderUserId());
    if (connection != null) {
      return mapSocialConnectionToConnection(connection);
    }

    throw new NoSuchConnectionException(connectionKey);
  }

  @Override
  public <A> Connection<A> getConnection(final Class<A> apiType, final String providerUserId) {
    String providerId = getProviderId(apiType);
    return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
  }

  @Override
  public <A> Connection<A> getPrimaryConnection(final Class<A> apiType) {
    String providerId = getProviderId(apiType);
    Connection<A> connection = (Connection<A>) findPrimaryConnectionInternal(providerId);
    if (connection == null) {
      throw new NotConnectedException(providerId);
    }

    return connection;
  }

  @Override
  public <A> Connection<A> findPrimaryConnection(final Class<A> apiType) {
    String providerId = getProviderId(apiType);
    return (Connection<A>) findPrimaryConnectionInternal(providerId);
  }

  private Connection<?> findPrimaryConnectionInternal(String providerId) {
    List<SC> connections = socialConnectionRepository.findByUserIdAndProviderId(
        userId, providerId,
        SORT_CREATED);
    if (connections.size() > 0) {
      return mapSocialConnectionToConnection(connections.get(0));
    }

    return null;
  }

  @Override
  public void addConnection(final Connection<?> connection) {
    try {
      ConnectionData data = connection.createData();
      SC socialConnection = createSocialConnectionFromData(data);
      socialConnectionRepository.save(socialConnection);
    } catch (DuplicateKeyException e) {
      logger.warn("addConnection failed with " + e.getMessage());
      throw new DuplicateConnectionException(connection.getKey());
    }

  }

  @Override
  public void updateConnection(final Connection<?> connection) {
    ConnectionData data = connection.createData();
    ConnectionKey connectionKey = connection.getKey();
    SC socialConnection = socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(
        userId,
        connectionKey.getProviderId(),
        connectionKey.getProviderUserId());
    if (socialConnection != null) {
      updateSocialConnectionFromConnectionData(data, socialConnection);
      socialConnectionRepository.save(socialConnection);
    }

  }

  @Override
  public void removeConnections(final String providerId) {
    socialConnectionRepository.deleteByUserIdAndProviderId(userId, providerId);
  }

  @Override
  public void removeConnection(final ConnectionKey connectionKey) {
    socialConnectionRepository
        .deleteByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.getProviderId(),
            connectionKey.getProviderUserId());
  }

  private <A> String getProviderId(Class<A> apiType) {
    return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
  }

  private Connection<?> mapSocialConnectionToConnection(
      final SocialConnection socialConnection) {
    ConnectionData connectionData = new ConnectionData(
        socialConnection.getProviderId(),
        socialConnection.getProviderUserId(),
        socialConnection.getDisplayName(),
        socialConnection.getProfileUrl(),
        socialConnection.getImageUrl(),
        encryptor.decrypt(socialConnection.getAccessToken()),
        encryptor.decrypt(socialConnection.getSecret()),
        encryptor.decrypt(socialConnection.getRefreshToken()),
        socialConnection.getExpireTime());
    if (providerConnectionFactoryMap.containsKey(connectionData.getProviderId())) {
      return providerConnectionFactoryMap.get(connectionData.getProviderId())
          .createConnection(connectionData);
    } else {
      throw new IllegalArgumentException(
          "No provider factory for providerId " + connectionData.getProviderId());
    }

  }

  protected abstract SC createSocialConnection();

  private SC createSocialConnectionFromData(final ConnectionData connectionData) {
    SC socialConnection = createSocialConnection();
    socialConnection.setUserId(userId);
    socialConnection.setProviderId(connectionData.getProviderId());
    socialConnection.setProviderUserId(connectionData.getProviderUserId());
    updateSocialConnectionFromConnectionData(connectionData, socialConnection);
    return socialConnection;
  }

  private void updateSocialConnectionFromConnectionData(
      final ConnectionData connectionData,
      final SocialConnection socialConnection) {
    socialConnection.setDisplayName(connectionData.getDisplayName());
    socialConnection.setProfileUrl(connectionData.getProfileUrl());
    socialConnection.setImageUrl(connectionData.getImageUrl());
    socialConnection.setAccessToken(encryptor.encrypt(connectionData.getAccessToken()));
    socialConnection.setSecret(encryptor.encrypt(connectionData.getSecret()));
    socialConnection.setRefreshToken(encryptor.encrypt(connectionData.getRefreshToken()));
    socialConnection.setExpireTime(connectionData.getExpireTime());
  }

}
