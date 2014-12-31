package com.jtbdevelopment.core.spring.social.dao

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

/**
 * Date: 12/16/14
 * Time: 1:17 PM
 */
@CompileStatic
abstract class AbstractConnectionRepository implements ConnectionRepository {
    private static Logger logger = LoggerFactory.getLogger(AbstractConnectionRepository.class)
    static AbstractSocialConnectionRepository userConnectionRepository
    static ConnectionFactoryLocator connectionFactoryLocator;
    static Map<String, ConnectionFactory<?>> providerConnectionFactoryMap = [:]
    static TextEncryptor encryptor

    private static final Sort SORT = new Sort(
            new Sort.Order(Sort.Direction.ASC, "providerId"),
            new Sort.Order(Sort.Direction.ASC, "creationTime"))

    protected final String userId;

    AbstractConnectionRepository(final String userId) {
        this.userId = userId
    }

    @Override
    MultiValueMap<String, Connection<?>> findAllConnections() {
        Iterable<SocialConnection> userConnections = userConnectionRepository.findByUserId(userId, SORT)
        MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
        providerConnectionFactoryMap.keySet().each {
            String key ->
                connections.put(key, (List<Connection<?>>) []);
        }
        userConnections.each {
            SocialConnection userConnection ->
                if (connections.get(userConnection.providerId).size() == 0) {
                    connections.put(userConnection.providerId, new LinkedList<Connection<?>>());
                }
                connections.add(userConnection.providerId, mapUserConnectionToConnection(userConnection));
        }
        return connections;
    }

    @Override
    List<Connection<?>> findConnections(final String providerId) {
        List<SocialConnection> connections = userConnectionRepository.findByUserIdAndProviderId(userId, providerId, SORT)
        return connections.collect {
            SocialConnection connection ->
                mapUserConnectionToConnection(connection)
        }
    }

    @Override
    def <A> List<Connection<A>> findConnections(final Class<A> apiType) {
        List<?> connections = findConnections(getProviderId(apiType));
        return (List<Connection<A>>) connections;
    }

    @Override
    MultiValueMap<String, Connection<?>> findConnectionsToUsers(
            final MultiValueMap<String, String> providerIdProviderUserIdList) {
        if (providerIdProviderUserIdList == null || providerIdProviderUserIdList.isEmpty()) {
            throw new IllegalArgumentException("Unable to execute find: no providerUserIds provided");
        }

        Map<String, List<Connection<?>>> userConnections = (Map<String, List<Connection<?>>>) providerIdProviderUserIdList.keySet().collectEntries {
            String providerId ->
                List<String> providerUserIds = providerIdProviderUserIdList.get(providerId)
                [
                        (providerId):
                                ((List<SocialConnection>) userConnectionRepository.findByUserIdAndProviderIdAndProviderUserIdIn(userId, providerId, providerUserIds, SORT)).collect {
                                    SocialConnection userConnection -> mapUserConnectionToConnection(userConnection)
                                }
                ]
        }

        MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
        userConnections.each {
            String userId, List<Connection<?>> connections ->
                connectionsForUsers.put(userId, connections)
        }
        return connectionsForUsers
    }

    @Override
    Connection<?> getConnection(final ConnectionKey connectionKey) {
        SocialConnection connection = userConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.providerId, connectionKey.providerUserId)
        if (connection) {
            return mapUserConnectionToConnection(connection);
        }
        throw new NoSuchConnectionException(connectionKey);
    }

    @Override
    def <A> Connection<A> getConnection(final Class<A> apiType, final String providerUserId) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
    }

    @Override
    def <A> Connection<A> getPrimaryConnection(final Class<A> apiType) {
        String providerId = getProviderId(apiType);
        Connection<A> connection = (Connection<A>) findPrimaryConnectionInternal(providerId);
        if (connection == null) {
            throw new NotConnectedException(providerId);
        }
        return connection;
    }

    @Override
    def <A> Connection<A> findPrimaryConnection(final Class<A> apiType) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) findPrimaryConnectionInternal(providerId);
    }

    private Connection<?> findPrimaryConnectionInternal(String providerId) {
        List<SocialConnection> connections = userConnectionRepository.findByUserIdAndProviderId(userId, providerId, SORT)
        if (connections.size() > 0) {
            return mapUserConnectionToConnection(connections[0])
        }
        return null;
    }

    abstract SocialConnection createSocialConnectionFromData(final ConnectionData data)

    @Override
    void addConnection(final Connection<?> connection) {
        try {
            ConnectionData data = connection.createData();
            SocialConnection userConnection = createSocialConnectionFromData(data)
            userConnectionRepository.save(userConnection)
        } catch (DuplicateKeyException e) {
            logger.warn("addConnection failed with " + e.message)
            throw new DuplicateConnectionException(connection.getKey());
        }
    }

    @Override
    void updateConnection(final Connection<?> connection) {
        ConnectionData data = connection.createData();
        ConnectionKey connectionKey = connection.key
        SocialConnection userConnection = userConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.providerId, connectionKey.providerUserId)
        if (userConnection) {
            userConnection.displayName = data.displayName
            userConnection.profileUrl = data.profileUrl
            userConnection.imageUrl = data.imageUrl
            userConnection.accessToken = data.accessToken ? encryptor.encrypt(data.accessToken) : null
            userConnection.secret = data.secret ? encryptor.encrypt(data.secret) : null
            userConnection.refreshToken = data.refreshToken ? encryptor.encrypt(data.refreshToken) : null
            userConnection.expireTime = data.expireTime
            userConnectionRepository.save(userConnection)
        }
    }

    @Override
    void removeConnections(final String providerId) {
        userConnectionRepository.deleteByUserIdAndProviderId(userId, providerId);
    }

    @Override
    void removeConnection(final ConnectionKey connectionKey) {
        userConnectionRepository.deleteByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.providerId, connectionKey.providerUserId)
    }

    private static <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
    }

    protected static Connection<?> mapUserConnectionToConnection(final SocialConnection connection) {
        ConnectionData connectionData = new ConnectionData(
                connection.providerId,
                connection.providerUserId,
                connection.displayName,
                connection.profileUrl,
                connection.imageUrl,
                connection.accessToken ? encryptor.decrypt(connection.accessToken) : null,
                connection.secret ? encryptor.decrypt(connection.secret) : null,
                connection.refreshToken ? encryptor.decrypt(connection.refreshToken) : null,
                connection.expireTime)
        providerConnectionFactoryMap[connectionData.providerId].createConnection(connectionData)
    }

}
