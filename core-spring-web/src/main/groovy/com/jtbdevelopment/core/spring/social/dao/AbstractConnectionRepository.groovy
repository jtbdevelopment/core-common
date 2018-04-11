package com.jtbdevelopment.core.spring.social.dao

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
abstract class AbstractConnectionRepository implements ConnectionRepository {
    private static Logger logger = LoggerFactory.getLogger(AbstractConnectionRepository.class)
    static AbstractSocialConnectionRepository socialConnectionRepository
    static ConnectionFactoryLocator connectionFactoryLocator
    static Map<String, ConnectionFactory<?>> providerConnectionFactoryMap = [:]
    static TextEncryptor encryptor

    protected static final Sort SORT_PID_CREATED = new Sort(
            new Sort.Order(Sort.Direction.ASC, "providerId"),
            new Sort.Order(Sort.Direction.ASC, "created")
    )
    protected static final Sort SORT_CREATED = new Sort(
            new Sort.Order(Sort.Direction.ASC, "created")
    )

    final String userId

    AbstractConnectionRepository(final String userId) {
        this.userId = userId
    }

    @Override
    MultiValueMap<String, Connection<?>> findAllConnections() {
        List<SocialConnection> socialConnections = socialConnectionRepository.findByUserId(userId, SORT_PID_CREATED)
        MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>()
        providerConnectionFactoryMap.keySet().each {
            String key ->
                connections.put(key, (List<Connection<?>>) [])
        }
        socialConnections.each {
            SocialConnection socialConnection ->
                connections.add(socialConnection.providerId, mapSocialConnectionToConnection(socialConnection))
        }
        return connections
    }

    @Override
    List<Connection<?>> findConnections(final String providerId) {
        List<SocialConnection> connections = socialConnectionRepository.findByUserIdAndProviderId(userId, providerId, SORT_CREATED)
        return connections.collect {
            SocialConnection connection ->
                mapSocialConnectionToConnection(connection)
        }
    }

    @Override
    <A> List<Connection<A>> findConnections(final Class<A> apiType) {
        List<?> connections = findConnections(getProviderId(apiType))
        return (List<Connection<A>>) connections
    }

    @Override
    MultiValueMap<String, Connection<?>> findConnectionsToUsers(
            final MultiValueMap<String, String> providerIdProviderUserIdList) {
        MultiValueMap<String, Connection<?>> connectionsByProvider = new LinkedMultiValueMap<String, Connection<?>>()
        if (providerIdProviderUserIdList != null) {
            providerIdProviderUserIdList.keySet().each {
                String providerId ->
                    List<String> providerUserIds = providerIdProviderUserIdList.get(providerId)
                    List<Connection<?>> resultArray = new ArrayList<Connection<?>>(providerUserIds.size())
                    providerUserIds.each { resultArray.add(null) }
                    connectionsByProvider.put(providerId, resultArray)
                    ((List<SocialConnection>) socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserIdIn(userId, providerId, providerUserIds, SORT_PID_CREATED)).each {
                        SocialConnection socialConnection ->
                            int index = providerUserIds.indexOf(socialConnection.providerUserId)
                            if (index >= 0) {
                                Connection<?> connection = mapSocialConnectionToConnection(socialConnection)
                                resultArray.set(index, connection)
                            }
                    }
            }
        }

        return connectionsByProvider
    }

    @Override
    Connection<?> getConnection(final ConnectionKey connectionKey) {
        SocialConnection connection = socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.providerId, connectionKey.providerUserId)
        if (connection) {
            return mapSocialConnectionToConnection(connection)
        }
        throw new NoSuchConnectionException(connectionKey)
    }

    @Override
    <A> Connection<A> getConnection(final Class<A> apiType, final String providerUserId) {
        String providerId = getProviderId(apiType)
        return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId))
    }

    @Override
    <A> Connection<A> getPrimaryConnection(final Class<A> apiType) {
        String providerId = getProviderId(apiType)
        Connection<A> connection = (Connection<A>) findPrimaryConnectionInternal(providerId)
        if (connection == null) {
            throw new NotConnectedException(providerId)
        }
        return connection
    }

    @Override
    <A> Connection<A> findPrimaryConnection(final Class<A> apiType) {
        String providerId = getProviderId(apiType)
        return (Connection<A>) findPrimaryConnectionInternal(providerId)
    }

    private Connection<?> findPrimaryConnectionInternal(String providerId) {
        List<SocialConnection> connections = socialConnectionRepository.findByUserIdAndProviderId(userId, providerId, SORT_CREATED)
        if (connections.size() > 0) {
            return mapSocialConnectionToConnection(connections[0])
        }
        return null
    }

    @Override
    void addConnection(final Connection<?> connection) {
        try {
            ConnectionData data = connection.createData()
            SocialConnection socialConnection = createSocialConnectionFromData(data)
            socialConnectionRepository.save(socialConnection)
        } catch (DuplicateKeyException e) {
            logger.warn("addConnection failed with " + e.message)
            throw new DuplicateConnectionException(connection.getKey())
        }
    }

    @Override
    void updateConnection(final Connection<?> connection) {
        ConnectionData data = connection.createData()
        ConnectionKey connectionKey = connection.key
        SocialConnection socialConnection = socialConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.providerId, connectionKey.providerUserId)
        if (socialConnection) {
            updateSocialConnectionFromConnectionData(data, socialConnection)
            socialConnectionRepository.save(socialConnection)
        }
    }

    @Override
    void removeConnections(final String providerId) {
        socialConnectionRepository.deleteByUserIdAndProviderId(userId, providerId)
    }

    @Override
    void removeConnection(final ConnectionKey connectionKey) {
        socialConnectionRepository.deleteByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.providerId, connectionKey.providerUserId)
    }

    private static <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId()
    }

    protected static Connection<?> mapSocialConnectionToConnection(
            final SocialConnection socialConnection) {
        ConnectionData connectionData = new ConnectionData(
                socialConnection.providerId,
                socialConnection.providerUserId,
                socialConnection.displayName,
                socialConnection.profileUrl,
                socialConnection.imageUrl,
                socialConnection.accessToken ? encryptor.decrypt(socialConnection.accessToken) : null,
                socialConnection.secret ? encryptor.decrypt(socialConnection.secret) : null,
                socialConnection.refreshToken ? encryptor.decrypt(socialConnection.refreshToken) : null,
                socialConnection.expireTime)
        if (providerConnectionFactoryMap.containsKey(connectionData.providerId)) {
            providerConnectionFactoryMap[connectionData.providerId].createConnection(connectionData)
        } else {
            throw new IllegalArgumentException('No provider factory for providerId ' + connectionData.providerId)
        }
    }

    abstract SocialConnection createSocialConnection()

    protected SocialConnection createSocialConnectionFromData(final ConnectionData connectionData) {
        SocialConnection socialConnection = createSocialConnection()
        socialConnection.userId = userId
        socialConnection.providerId = connectionData.providerId
        socialConnection.providerUserId = connectionData.providerUserId
        updateSocialConnectionFromConnectionData(connectionData, socialConnection)
        return socialConnection
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void updateSocialConnectionFromConnectionData(
            final ConnectionData connectionData, final SocialConnection socialConnection) {
        socialConnection.displayName = connectionData.displayName
        socialConnection.profileUrl = connectionData.profileUrl
        socialConnection.imageUrl = connectionData.imageUrl
        socialConnection.accessToken = connectionData.accessToken ? encryptor.encrypt(connectionData.accessToken) : null
        socialConnection.secret = connectionData.secret ? encryptor.encrypt(connectionData.secret) : null
        socialConnection.refreshToken = connectionData.refreshToken ? encryptor.encrypt(connectionData.refreshToken) : null
        socialConnection.expireTime = connectionData.expireTime
    }
}
