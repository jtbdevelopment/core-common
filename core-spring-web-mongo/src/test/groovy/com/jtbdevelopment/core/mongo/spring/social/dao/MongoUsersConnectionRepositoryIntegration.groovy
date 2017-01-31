package com.jtbdevelopment.core.mongo.spring.social.dao

import com.jtbdevelopment.core.mongo.spring.AbstractCoreMongoConfiguration
import com.jtbdevelopment.core.mongo.spring.AbstractMongoDefaultSpringContextIntegration
import com.jtbdevelopment.core.mongo.spring.converters.StringToZonedDateTimeConverter
import com.jtbdevelopment.core.mongo.spring.converters.ZonedDateTimeToStringConverter
import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository
import com.jtbdevelopment.core.spring.social.dao.utility.*
import com.mongodb.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.*
import org.springframework.social.connect.support.ConnectionFactoryRegistry
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 1/4/2015
 * Time: 8:19 PM
 *
 * Significantly derived from spring's JdbcUsersConnectionRepositoryTest
 */
class MongoUsersConnectionRepositoryIntegration extends AbstractMongoDefaultSpringContextIntegration {
    @SuppressWarnings("GroovyUnusedDeclaration")
    @Configuration
    private static class IntegrationSocialConfiguration extends AbstractCoreMongoConfiguration {
        @Bean
        @Autowired
        ConnectionFactoryRegistry connectionFactoryLocator() {
            ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
            registry.addConnectionFactory(new FakeFacebookConnectionFactory())
            registry.addConnectionFactory(new FakeTwitterConnectionFactory())
            return registry;
        }
    }

    private static final String CREATED_COLUMN = 'created'
    private static final String USERID_COLUMN = 'userId'
    private static final String PROVIDERID_COLUMN = 'providerId'
    private static final String PROVIDERUSERID_COLUMN = 'providerUserId'
    private static final String DISPLAYNAME_COLUMN = 'displayName'
    private static final String PROFILE_COLUMN = 'profileUrl'
    private static final String IMAGE_COLUMN = 'imageUrl'
    private static final String TOKEN_COLUMN = 'accessToken'
    private static final String SECRET_COLUMN = 'secret'
    private static final String REFRESH_COLUMN = 'refreshToken'
    private static final String EXPIRE_COLUMN = 'expireTime'
    private static final ZoneId GMT = ZoneId.of("GMT")

    //  Equiv of insertTwitter
    public static final USER1_TWITTER1 = [
            (USERID_COLUMN)        : '1',
            (PROVIDERID_COLUMN)    : FakeTwitterApi.TWITTER,
            (PROVIDERUSERID_COLUMN): '1',
            (CREATED_COLUMN)       : ZonedDateTime.now(GMT),
            (DISPLAYNAME_COLUMN)   : '@kdonald',
            (PROFILE_COLUMN)       : 'http://twitter.com/kdonald',
            (IMAGE_COLUMN)         : 'http://twitter.com/kdonald/picture',
            (TOKEN_COLUMN)         : '123456789',
            (SECRET_COLUMN)        : '987654321',
            (REFRESH_COLUMN)       : null,
            (EXPIRE_COLUMN)        : null
    ]
    //  Equiv of insertFacebook
    public static final USER1_FACEBOOK9 = [
            (USERID_COLUMN)        : '1',
            (PROVIDERID_COLUMN)    : FakeFacebookApi.FACEBOOK,
            (PROVIDERUSERID_COLUMN): '9',
            (CREATED_COLUMN)       : ZonedDateTime.now(GMT),
            (DISPLAYNAME_COLUMN)   : null,
            (PROFILE_COLUMN)       : null,
            (IMAGE_COLUMN)         : null,
            (TOKEN_COLUMN)         : '234567890',
            (SECRET_COLUMN)        : null,
            (REFRESH_COLUMN)       : '345678901',
            (EXPIRE_COLUMN)        : System.currentTimeMillis() + 3600000
    ]

    //  equiv of insertFacebookConnection2
    public static final USER1_FACEBOOK10 = [
            (USERID_COLUMN)        : '1',
            (PROVIDERID_COLUMN)    : FakeFacebookApi.FACEBOOK,
            (PROVIDERUSERID_COLUMN): '10',
            (CREATED_COLUMN)       : ZonedDateTime.now(GMT),
            (DISPLAYNAME_COLUMN)   : null,
            (PROFILE_COLUMN)       : null,
            (IMAGE_COLUMN)         : null,
            (TOKEN_COLUMN)         : '456789012',
            (SECRET_COLUMN)        : null,
            (REFRESH_COLUMN)       : '56789012',
            (EXPIRE_COLUMN)        : System.currentTimeMillis() + 3600000
    ]

    //  equiv of insertFacebookConnection3
    public static final USER2_FACEBOOK11 = [
            (USERID_COLUMN)        : '2',
            (PROVIDERID_COLUMN)    : FakeFacebookApi.FACEBOOK,
            (PROVIDERUSERID_COLUMN): '11',
            (CREATED_COLUMN)       : ZonedDateTime.now(GMT),
            (DISPLAYNAME_COLUMN)   : null,
            (PROFILE_COLUMN)       : null,
            (IMAGE_COLUMN)         : null,
            (TOKEN_COLUMN)         : '456789012',
            (SECRET_COLUMN)        : null,
            (REFRESH_COLUMN)       : '56789012',
            (EXPIRE_COLUMN)        : System.currentTimeMillis() + 3600000
    ]

    //  equiv of insertFacebookConnectionSameFacebookUser
    public static final USER2_FACEBOOK9DUPE = [
            (USERID_COLUMN)        : '2',
            (PROVIDERID_COLUMN)    : FakeFacebookApi.FACEBOOK,
            (PROVIDERUSERID_COLUMN): '9',
            (CREATED_COLUMN)       : ZonedDateTime.now(GMT),
            (DISPLAYNAME_COLUMN)   : null,
            (PROFILE_COLUMN)       : null,
            (IMAGE_COLUMN)         : null,
            (TOKEN_COLUMN)         : '234567890',
            (SECRET_COLUMN)        : null,
            (REFRESH_COLUMN)       : '345678901',
            (EXPIRE_COLUMN)        : System.currentTimeMillis() + 3600000
    ]

    //  Equiv of insertTwitter
    public static final USER1_NEWCO1 = [
            (USERID_COLUMN)        : '1',
            (PROVIDERID_COLUMN)    : 'NEWCONNECTION',   // unregisted
            (PROVIDERUSERID_COLUMN): '1',
            (CREATED_COLUMN)       : ZonedDateTime.now(GMT),
            (DISPLAYNAME_COLUMN)   : '@kdonald',
            (PROFILE_COLUMN)       : 'http://twitter.com/kdonald',
            (IMAGE_COLUMN)         : 'http://twitter.com/kdonald/picture',
            (TOKEN_COLUMN)         : '123456789',
            (SECRET_COLUMN)        : '987654321',
            (REFRESH_COLUMN)       : null,
            (EXPIRE_COLUMN)        : null
    ]

    protected Map<String, FakeConnectionFactory> providers;
    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected TextEncryptor textEncryptor
    protected AbstractUsersConnectionRepository socialConnectionRepository
    protected ConnectionRepository user1SocialConnectionRepository
    protected ZonedDateTimeToStringConverter zonedDateTimeToStringConverter
    protected StringToZonedDateTimeConverter stringToZonedDateTimeConverter

    DBCollection collection;

    @Before
    void setUp() {
        textEncryptor = context.getBean(TextEncryptor.class)
        providers = [
                (FakeFacebookApi.FACEBOOK): new FakeFacebookConnectionFactory(),
                (FakeTwitterApi.TWITTER)  : new FakeTwitterConnectionFactory()
        ]
        connectionFactoryLocator = [
                registeredProviderIds: {
                    return providers.keySet()
                },
                getConnectionFactory : {
                    Object s ->
                        if (s instanceof String) {
                            return providers[s]
                        }
                        if (s.is(FakeTwitterApi.class)) {
                            return providers[FakeTwitterApi.TWITTER]
                        }
                        if (s.is(FakeFacebookApi.class)) {
                            return providers[FakeFacebookApi.FACEBOOK]
                        }
                        null
                }
        ] as ConnectionFactoryLocator

        assert db.collectionExists('socialConnections')
        collection = db.getCollection('socialConnections')
        collection.remove(new BasicDBObject())

        socialConnectionRepository = context.getBean(AbstractUsersConnectionRepository.class)
        user1SocialConnectionRepository = socialConnectionRepository.createConnectionRepository('1')
        stringToZonedDateTimeConverter = context.getBean(StringToZonedDateTimeConverter.class)
        zonedDateTimeToStringConverter = context.getBean(ZonedDateTimeToStringConverter.class)
    }

    @Test
    public void testCollectionConfiguration() {
        List<DBObject> indices = collection.indexInfo
        boolean userProviderIdCreatedFound = false
        boolean userProviderIdProviderUserIdFound = false
        indices.each {
            DBObject it ->
                switch (it.get('name')) {
                    case 'sc_uidpidc':
                        userProviderIdCreatedFound = true
                        assert it.get('unique') == Boolean.TRUE
                        BasicDBObject key = it.get('key') as BasicDBObject
                        assert key.size() == 3
                        def iterator = key.iterator()
                        Map.Entry<Object, Object> sub = iterator.next()
                        assert sub.value == 1
                        assert sub.key == 'userId'
                        sub = iterator.next()
                        assert sub.value == 1
                        assert sub.key == 'providerId'
                        sub = iterator.next()
                        assert sub.value == 1
                        assert sub.key == 'created'
                        assert !iterator.hasNext()
                        break;
                    case 'sc_pk':
                        userProviderIdProviderUserIdFound = true
                        assert it.get('unique') == Boolean.TRUE
                        BasicDBObject key = it.get('key') as BasicDBObject
                        assert key.size() == 3
                        def iterator = key.iterator()
                        Map.Entry<Object, Object> sub = iterator.next()
                        assert sub.value == 1
                        assert sub.key == 'userId'
                        sub = iterator.next()
                        assert sub.value == 1
                        assert sub.key == 'providerId'
                        sub = iterator.next()
                        assert sub.value == 1
                        assert sub.key == 'providerUserId'
                        assert !iterator.hasNext()
                        break;
                }
        }
        assert userProviderIdCreatedFound
        assert userProviderIdProviderUserIdFound
    }


    @Test
    public void testFindUserIdWithConnection() {
        insertSocialConnectionRow(USER1_FACEBOOK9);
        List<String> userIds = socialConnectionRepository.findUserIdsWithConnection(user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class));
        assert '1' == userIds.get(0)
    }

    @Test
    public void testFindUserIdWithConnectionNoSuchConnection() {
        org.springframework.social.connect.Connection<?> connection = providers[FakeFacebookApi.FACEBOOK].createConnection(
                createConnectionData([(PROVIDERUSERID_COLUMN): '12345', (PROVIDERID_COLUMN): FakeFacebookApi.FACEBOOK])
        );
        assert 0 == socialConnectionRepository.findUserIdsWithConnection(connection).size();
    }

    @Test
    public void testFindUserIdWithConnectionMultipleConnectionsToSameProviderUser() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER2_FACEBOOK9DUPE)
        List<String> localUserIds = socialConnectionRepository.findUserIdsWithConnection(user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class));
        assert 2 == localUserIds.size();
        assert "1" == localUserIds.get(0);
        assert "2" == localUserIds.get(1);
    }

    @Test
    public void testFindUserIdWithConnectionNoConnectionWithWorkingConnectionSignUp() {
        org.springframework.social.connect.Connection<?> connection = providers[FakeFacebookApi.FACEBOOK].createConnection(
                createConnectionData([(PROVIDERUSERID_COLUMN): '12345', (PROVIDERID_COLUMN): FakeFacebookApi.FACEBOOK])
        );
        try {
            socialConnectionRepository.connectionSignUp = [
                    execute: {
                        org.springframework.social.connect.Connection<?> c ->
                            return 'batman'
                    }] as ConnectionSignUp
            List<String> userIds = socialConnectionRepository.findUserIdsWithConnection(connection);
            assert 1 == userIds.size();
            assert "batman" == userIds.get(0);
        } finally {
            socialConnectionRepository.connectionSignUp = null;
        }
    }

    @Test
    public void testFindUserIdWithConnectionNoConnectionWithConnectionSignUpReturningNull() {
        org.springframework.social.connect.Connection<?> connection = providers[FakeFacebookApi.FACEBOOK].createConnection(
                createConnectionData([(PROVIDERUSERID_COLUMN): '12345', (PROVIDERID_COLUMN): FakeFacebookApi.FACEBOOK])
        );
        try {
            socialConnectionRepository.connectionSignUp = [
                    execute: {
                        org.springframework.social.connect.Connection<?> c ->
                            return null
                    }] as ConnectionSignUp
            List<String> userIds = socialConnectionRepository.findUserIdsWithConnection(connection);
            assert 0 == userIds.size();
        } finally {
            socialConnectionRepository.connectionSignUp = null;
        }
    }

    @Test
    public void testFindUserIdsConnectedTo() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER2_FACEBOOK11)
        Set<String> localUserIds = socialConnectionRepository.findUserIdsConnectedTo("facebook", new HashSet<String>(Arrays.asList("9", "11")));
        assert 2 == localUserIds.size();
        assert localUserIds.contains("1");
        assert localUserIds.contains("2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindAllConnections() {
        insertSocialConnectionRow(USER1_TWITTER1)
        insertSocialConnectionRow(USER1_FACEBOOK9)
        MultiValueMap<String, org.springframework.social.connect.Connection<?>> connections = user1SocialConnectionRepository.findAllConnections();
        assert 2 == connections.size();
        org.springframework.social.connect.Connection<FakeFacebookApi> facebook = connections.getFirst(FakeFacebookApi.FACEBOOK);
        compareConnectionToMap(facebook, USER1_FACEBOOK9)
        org.springframework.social.connect.Connection<FakeTwitterApi> twitter = connections.getFirst(FakeTwitterApi.TWITTER);
        compareConnectionToMap(twitter, USER1_TWITTER1);
    }

    @Test
    public void testFindAllConnectionsMultipleConnectionResults() {
        insertSocialConnectionRow(USER1_TWITTER1)
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER1_FACEBOOK10)
        MultiValueMap<String, org.springframework.social.connect.Connection<?>> connections = user1SocialConnectionRepository.findAllConnections();
        assert 2 == connections.size();
        assert 2 == connections.get(FakeFacebookApi.FACEBOOK).size();
        assert 1 == connections.get(FakeTwitterApi.TWITTER).size();
    }

    @Test
    public void testFindAllConnectionsEmptyResult() {
        MultiValueMap<String, org.springframework.social.connect.Connection<?>> connections = user1SocialConnectionRepository.findAllConnections();
        assert 2 == connections.size();
        assert 0 == connections.get(FakeFacebookApi.FACEBOOK).size();
        assert 0 == connections.get(FakeTwitterApi.TWITTER).size();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSuchConnectionFactory() {
        insertSocialConnectionRow(USER1_NEWCO1);
        user1SocialConnectionRepository.findAllConnections();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindConnectionsByProviderId() {
        insertSocialConnectionRow(USER1_TWITTER1)
        List<org.springframework.social.connect.Connection<?>> connections = user1SocialConnectionRepository.findConnections(FakeTwitterApi.TWITTER);
        assert 1 == connections.size();
        compareConnectionToMap(connections.get(0), USER1_TWITTER1);
    }

    @Test
    public void testFindConnectionsByProviderIdEmptyResult() {
        assert user1SocialConnectionRepository.findConnections(FakeFacebookApi.FACEBOOK).isEmpty();
    }

    @Test
    public void testFindConnectionsByApi() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER1_FACEBOOK10)
        List<org.springframework.social.connect.Connection<?>> connections = user1SocialConnectionRepository.findConnections(FakeFacebookApi.class);
        assert 2 == connections.size();
        compareConnectionToMap(connections.get(0), USER1_FACEBOOK9);
        compareConnectionToMap(connections.get(1), USER1_FACEBOOK10);
    }

    @Test
    public void testFindConnectionsByApiEmptyResult() {
        assert user1SocialConnectionRepository.findConnections(FakeFacebookApi.class).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindConnectionsToUsers() {
        insertSocialConnectionRow(USER1_TWITTER1)
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER1_FACEBOOK10)

        MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
        providerUsers.add(FakeFacebookApi.FACEBOOK, "10");
        providerUsers.add(FakeFacebookApi.FACEBOOK, "9");
        providerUsers.add(FakeTwitterApi.TWITTER, "1");
        MultiValueMap<String, org.springframework.social.connect.Connection<?>> connectionsForUsers = user1SocialConnectionRepository.findConnectionsToUsers(providerUsers);
        assert 2 == connectionsForUsers.size();
        assert "10" == connectionsForUsers.getFirst(FakeFacebookApi.FACEBOOK).getKey().getProviderUserId();
        compareConnectionToMap(connectionsForUsers.get(FakeFacebookApi.FACEBOOK).get(1), USER1_FACEBOOK9)
        compareConnectionToMap(connectionsForUsers.get(FakeTwitterApi.TWITTER).get(0), USER1_TWITTER1)
    }

    //  This is the one significant departure from the Jdbc Test
    //  If there are no results JDBC returns an empty map
    //  We return a map of asked for providers to null filled lists
    //  The caller needs to deal with partial results anyway and we couldn't actually find a caller
    @Test
    public void testFindConnectionsToUsersEmptyResult() {
        MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
        providerUsers.add(FakeFacebookApi.FACEBOOK, "1");

        def users = user1SocialConnectionRepository.findConnectionsToUsers(providerUsers)
        assert users.size() == 1
        assert users.get(FakeFacebookApi.FACEBOOK).size() == 1
        assert users.get(FakeFacebookApi.FACEBOOK).get(0) == null
    }

    //  This test is another minor departure
    @Test
    public void testFindConnectionsToUsersNullInput() {
        assert user1SocialConnectionRepository.findConnectionsToUsers(null).isEmpty()
    }

    //  This test is another minor departure
    @Test
    public void testFindConnectionsToUsersNEmptyInput() {
        MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
        assert user1SocialConnectionRepository.findConnectionsToUsers(providerUsers).isEmpty()
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindConnectionByKey() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        compareConnectionToMap(
                user1SocialConnectionRepository.getConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "9")),
                USER1_FACEBOOK9
        );
    }

    @Test(expected = NoSuchConnectionException.class)
    public void testFindConnectionByKeyNoSuchConnection() {
        user1SocialConnectionRepository.getConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "bogus"));
    }

    @Test
    public void findConnectionByApiToUser() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER1_FACEBOOK10)
        compareConnectionToMap(user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "9"), USER1_FACEBOOK9);
        assert "10" == user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "10").getKey().getProviderUserId();
    }

    @Test(expected = NoSuchConnectionException.class)
    public void testFindConnectionByApiToUserNoSuchConnection() {
        user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "9");
    }

    @Test
    public void testFindPrimaryConnection() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        compareConnectionToMap(user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class), USER1_FACEBOOK9);
    }

    @Test
    public void testRemoveConnections() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER1_FACEBOOK10)
        insertSocialConnectionRow(USER1_TWITTER1)   //  More rigorous
        def query = new QueryBuilder().start(USERID_COLUMN).is('1').get()
        assert collection.count(query) == 3
        user1SocialConnectionRepository.removeConnections(FakeFacebookApi.FACEBOOK);
        assert collection.count(query) == 1
    }

    //  More rigorous test
    @Test
    public void testRemoveConnectionsToProviderNoOp() {
        insertSocialConnectionRow(USER1_FACEBOOK9)  //  More rigorous
        assert collection.count() == 1
        user1SocialConnectionRepository.removeConnections(FakeTwitterApi.TWITTER);
        assert collection.count() == 1
    }

    @Test
    public void testRemoveConnection() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER1_FACEBOOK10)  // More rigorous test
        def query = new QueryBuilder().start(USERID_COLUMN).is('1').get()
        assert collection.count(query) == 2
        user1SocialConnectionRepository.removeConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "9"));
        assert collection.count(query) == 1
    }

    @Test
    public void testRemoveConnectionNoOp() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        assert collection.count() == 1
        user1SocialConnectionRepository.removeConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "1"));
        assert collection.count() == 1
    }

    @Test
    public void testFindPrimaryConnectionSelectFromMultipleByCreationTime() {
        insertSocialConnectionRow(USER1_FACEBOOK10)
        insertSocialConnectionRow(USER1_FACEBOOK9)
        compareConnectionToMap(user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class), USER1_FACEBOOK9);
    }

    @Test(expected = NotConnectedException.class)
    public void testFindPrimaryConnectionNotConnected() {
        user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class);
    }

    @Test
    public void testAddConnection() {
        org.springframework.social.connect.Connection<?> connection = providers[FakeFacebookApi.FACEBOOK].createConnection(
                createConnectionData(USER1_FACEBOOK10)
        )
        user1SocialConnectionRepository.addConnection(connection);
        org.springframework.social.connect.Connection<?> loaded = user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class);
        compareConnectionToMap(loaded, USER1_FACEBOOK10)
    }

    @Test(expected = DuplicateConnectionException.class)
    public void testAddConnectionDuplicate() {
        org.springframework.social.connect.Connection<?> connection = providers[FakeFacebookApi.FACEBOOK].createConnection(
                createConnectionData(USER1_FACEBOOK10)
        )
        user1SocialConnectionRepository.addConnection(connection);
        user1SocialConnectionRepository.addConnection(connection);
    }

    @Test
    public void testUpdateConnectionProfileFields() {
        insertSocialConnectionRow(USER1_TWITTER1)
        org.springframework.social.connect.Connection<FakeTwitterApi> twitter = user1SocialConnectionRepository.getPrimaryConnection(FakeTwitterApi.class);
        compareConnectionToMap(twitter, USER1_TWITTER1)
        Map newValues = [:]
        newValues.putAll(USER1_TWITTER1)
        newValues[PROFILE_COLUMN] = "http://twitter.com/kdonald/a_new_picture"
        twitter = providers[FakeFacebookApi.FACEBOOK].createConnection(
                createConnectionData(newValues)
        )
        user1SocialConnectionRepository.updateConnection(twitter);
        org.springframework.social.connect.Connection<FakeTwitterApi> twitter2 = user1SocialConnectionRepository.getPrimaryConnection(FakeTwitterApi.class);
        compareConnectionToMap(twitter2, newValues)
    }

    @Test
    public void testUpdateConnectionAccessFields() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        org.springframework.social.connect.Connection<FakeFacebookApi> facebook = user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class);
        compareConnectionToMap(facebook, USER1_FACEBOOK9)
        Map newValues = [:]
        newValues.putAll(USER1_FACEBOOK9)
        def newToken = '765432109'
        def newRefresh = '654321098'
        newValues[TOKEN_COLUMN] = newToken
        newValues[REFRESH_COLUMN] = newRefresh
        facebook = providers[FakeFacebookApi.FACEBOOK].createConnection(
                createConnectionData(newValues)
        )
        user1SocialConnectionRepository.updateConnection(facebook);
        org.springframework.social.connect.Connection<FakeFacebookApi> facebook2 = user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class);
        ConnectionData data = facebook2.createData();
        assert newToken == data.accessToken;
        assert newRefresh == data.refreshToken
    }

    @Test
    public void testFindPrimaryConnectionAfterRemove() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER1_FACEBOOK10)
        // 9 is the providerUserId of the first Facebook connection
        user1SocialConnectionRepository.removeConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "9"));
        assert 1 == user1SocialConnectionRepository.findConnections(FakeFacebookApi.class).size();
        compareConnectionToMap(user1SocialConnectionRepository.findPrimaryConnection(FakeFacebookApi.class), USER1_FACEBOOK10);
    }

    private void compareConnectionToMap(
            final org.springframework.social.connect.Connection<?> connection, final Map values) {
        assert connection instanceof FakeConnection
        assert connection.displayName == values[DISPLAYNAME_COLUMN]
        assert connection.profileUrl == values[PROFILE_COLUMN]
        assert connection.key.providerId == values[PROVIDERID_COLUMN]
        assert connection.key.providerUserId == values[PROVIDERUSERID_COLUMN]
        assert connection.imageUrl == values[IMAGE_COLUMN]
        assert connection.accessToken == values[TOKEN_COLUMN]
        assert connection.refreshToken == values[REFRESH_COLUMN]
        assert connection.secret == values[SECRET_COLUMN]
        assert connection.expireTime == values[EXPIRE_COLUMN]
    }

    protected ConnectionData createConnectionData(final Map values) {
        new ConnectionData(
                values[PROVIDERID_COLUMN],
                values[PROVIDERUSERID_COLUMN],
                values[DISPLAYNAME_COLUMN],
                values[PROFILE_COLUMN],
                values[IMAGE_COLUMN],
                values[TOKEN_COLUMN],
                values[SECRET_COLUMN],
                values[REFRESH_COLUMN],
                values[EXPIRE_COLUMN])
    }

    protected WriteResult insertSocialConnectionRow(final Map values) {
        Map newValues = [:]
        newValues.putAll(values)
        newValues[CREATED_COLUMN] = zonedDateTimeToStringConverter.convert(values[CREATED_COLUMN])
        newValues[TOKEN_COLUMN] = values[TOKEN_COLUMN] ? textEncryptor.encrypt(values[TOKEN_COLUMN]) : null
        newValues[REFRESH_COLUMN] = values[REFRESH_COLUMN] ? textEncryptor.encrypt(values[REFRESH_COLUMN]) : null
        newValues[SECRET_COLUMN] = values[SECRET_COLUMN] ? textEncryptor.encrypt(values[SECRET_COLUMN]) : null
        collection.insert(new BasicDBObjectBuilder().start(newValues).get())
    }
}
