package com.jtbdevelopment.core.mongo.spring.social.dao

import com.jtbdevelopment.core.mongo.spring.AbstractMongoIntegration
import com.jtbdevelopment.core.mongo.spring.converters.StringToZonedDateTimeConverter
import com.jtbdevelopment.core.mongo.spring.converters.ZonedDateTimeToStringConverter
import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository
import com.jtbdevelopment.core.spring.social.dao.utility.*
import com.mongodb.*
import org.junit.Before
import org.junit.Test
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.ConnectionData
import org.springframework.social.connect.ConnectionFactoryLocator
import org.springframework.social.connect.ConnectionKey
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.ConnectionSignUp
import org.springframework.social.connect.NoSuchConnectionException
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 1/4/2015
 * Time: 8:19 PM
 */
class MongoUsersConnectionRepositoryIntegration extends AbstractMongoIntegration {

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
    public static final ZoneId GMT = ZoneId.of("GMT")

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
    public void findConnectionByKey() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        compareConnectionToMap(
                user1SocialConnectionRepository.getConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "9")),
                USER1_FACEBOOK9
        );
    }

    @Test(expected=NoSuchConnectionException.class)
    public void findConnectionByKeyNoSuchConnection() {
        user1SocialConnectionRepository.getConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "bogus"));
    }

    @Test
    public void findConnectionByApiToUser() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        insertSocialConnectionRow(USER1_FACEBOOK10)
        compareConnectionToMap(user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "9"), USER1_FACEBOOK9);
        assert "10" == user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "10").getKey().getProviderUserId();
    }

    @Test(expected=NoSuchConnectionException.class)
    public void findConnectionByApiToUserNoSuchConnection() {
        user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "9");
    }

    @Test
    public void findPrimaryConnection() {
        insertSocialConnectionRow(USER1_FACEBOOK9)
        compareConnectionToMap(user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class), USER1_FACEBOOK9);
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

    /*

public class JdbcUsersConnectionRepositoryTest {

    private EmbeddedDatabase database;

    private boolean testMySqlCompatiblity;

    private ConnectionFactoryRegistry connectionFactoryRegistry;

    private TestFacebookConnectionFactory connectionFactory;

    private JdbcTemplate dataAccessor;

    private JdbcUsersConnectionRepository usersConnectionRepository;

    private ConnectionRepository connectionRepository;

    @Before
    public void setUp() {
        EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
        if (testMySqlCompatiblity) {
            factory.setDatabaseConfigurer(new MySqlCompatibleH2DatabaseConfigurer());
        } else {
            factory.setDatabaseType(EmbeddedDatabaseType.H2);
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(getSchemaSql(), getClass()));
        factory.setDatabasePopulator(populator);
        database = factory.getDatabase();
        dataAccessor = new JdbcTemplate(database);
        connectionFactoryRegistry = new ConnectionFactoryRegistry();
        connectionFactory = new TestFacebookConnectionFactory();
        connectionFactoryRegistry.addConnectionFactory(connectionFactory);
        usersConnectionRepository = new JdbcUsersConnectionRepository(database, connectionFactoryRegistry, Encryptors.noOpText());
        if (!getTablePrefix().equals("")) {
            usersConnectionRepository.setTablePrefix(getTablePrefix());
        }
        connectionRepository = usersConnectionRepository.createConnectionRepository("1");
    }

    @Test
    public void findPrimaryConnectionSelectFromMultipleByRank() {
        insertFacebookConnection2();
        insertFacebookConnection();
        assertFacebookConnection(connectionRepository.getPrimaryConnection(TestFacebookApi.class));
    }

    @Test(expected=NotConnectedException.class)
    public void findPrimaryConnectionNotConnected() {
        connectionRepository.getPrimaryConnection(TestFacebookApi.class);
    }

    @Test
    public void removeConnections() {
        insertFacebookConnection();
        insertFacebookConnection2();
        assertTrue(dataAccessor.queryForObject("select exists (select 1 from " + getTablePrefix() + "UserConnection where providerId = 'facebook')", Boolean.class));
        connectionRepository.removeConnections("facebook");
        assertFalse(dataAccessor.queryForObject("select exists (select 1 from " + getTablePrefix() + "UserConnection where providerId = 'facebook')", Boolean.class));
    }

    @Test
    public void removeConnectionsToProviderNoOp() {
        connectionRepository.removeConnections("twitter");
    }

    @Test
    public void removeConnection() {
        insertFacebookConnection();
        assertTrue(dataAccessor.queryForObject("select exists (select 1 from " + getTablePrefix() + "UserConnection where providerId = 'facebook')", Boolean.class));
        connectionRepository.removeConnection(new ConnectionKey("facebook", "9"));
        assertFalse(dataAccessor.queryForObject("select exists (select 1 from " + getTablePrefix() + "UserConnection where providerId = 'facebook')", Boolean.class));
    }

    @Test
    public void removeConnectionNoOp() {
        connectionRepository.removeConnection(new ConnectionKey("facebook", "1"));
    }

    @Test
    public void addConnection() {
        Connection<TestFacebookApi> connection = connectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600L));
        connectionRepository.addConnection(connection);
        Connection<TestFacebookApi> restoredConnection = connectionRepository.getPrimaryConnection(TestFacebookApi.class);
        assertEquals(connection, restoredConnection);
        assertNewConnection(restoredConnection);
    }

    @Test(expected=DuplicateConnectionException.class)
    public void addConnectionDuplicate() {
        Connection<TestFacebookApi> connection = connectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600L));
        connectionRepository.addConnection(connection);
        connectionRepository.addConnection(connection);
    }

    @Test
    public void updateConnectionProfileFields() {
        connectionFactoryRegistry.addConnectionFactory(new TestTwitterConnectionFactory());
        insertTwitterConnection();
        Connection<TestTwitterApi> twitter = connectionRepository.getPrimaryConnection(TestTwitterApi.class);
        assertEquals("http://twitter.com/kdonald/picture", twitter.getImageUrl());
        twitter.sync();
        assertEquals("http://twitter.com/kdonald/a_new_picture", twitter.getImageUrl());
        connectionRepository.updateConnection(twitter);
        Connection<TestTwitterApi> twitter2 = connectionRepository.getPrimaryConnection(TestTwitterApi.class);
        assertEquals("http://twitter.com/kdonald/a_new_picture", twitter2.getImageUrl());
    }

    @Test
    public void updateConnectionAccessFields() {
        insertFacebookConnection();
        Connection<TestFacebookApi> facebook = connectionRepository.getPrimaryConnection(TestFacebookApi.class);
        assertEquals("234567890", facebook.getApi().getAccessToken());
        facebook.refresh();
        connectionRepository.updateConnection(facebook);
        Connection<TestFacebookApi> facebook2 = connectionRepository.getPrimaryConnection(TestFacebookApi.class);
        assertEquals("765432109", facebook2.getApi().getAccessToken());
        ConnectionData data = facebook.createData();
        assertEquals("654321098", data.getRefreshToken());
    }

    @Test
    public void findPrimaryConnection_afterRemove() {
        insertFacebookConnection();
        insertFacebookConnection2();
        // 9 is the providerUserId of the first Facebook connection
        connectionRepository.removeConnection(new ConnectionKey("facebook", "9"));
        assertEquals(1, connectionRepository.findConnections(TestFacebookApi.class).size());
        assertNotNull(connectionRepository.findPrimaryConnection(TestFacebookApi.class));
    }

    // subclassing hooks

    protected String getTablePrefix() {
        return "";
    }

    protected String getSchemaSql() {
        return "JdbcUsersConnectionRepository.sql";
    }

	private void insertTwitterConnection() {
		dataAccessor.update("insert into " + getTablePrefix() + "UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "twitter", "1", 1, "@kdonald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "123456789", "987654321", null, null);
	}

	private void insertFacebookConnection() {
		dataAccessor.update("insert into " + getTablePrefix() + "UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "facebook", "9", 1, null, null, null, "234567890", null, "345678901", System.currentTimeMillis() + 3600000);
	}

	private void insertFacebookConnection2() {
		dataAccessor.update("insert into " + getTablePrefix() + "UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "facebook", "10", 2, null, null, null, "456789012", null, "56789012", System.currentTimeMillis() + 3600000);
	}

	private void insertFacebookConnection3() {
		dataAccessor.update("insert into " + getTablePrefix() + "UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"2", "facebook", "11", 2, null, null, null, "456789012", null, "56789012", System.currentTimeMillis() + 3600000);
	}

	private void insertFacebookConnectionSameFacebookUser() {
		dataAccessor.update("insert into " + getTablePrefix() + "UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"2", "facebook", "9", 1, null, null, null, "234567890", null, "345678901", System.currentTimeMillis() + 3600000);
	}

    private void assertNewConnection(Connection<TestFacebookApi> connection) {
        assertEquals("facebook", connection.getKey().getProviderId());
        assertEquals("9", connection.getKey().getProviderUserId());
        assertEquals("Keith Donald", connection.getDisplayName());
        assertEquals("http://facebook.com/keith.donald", connection.getProfileUrl());
        assertEquals("http://facebook.com/keith.donald/picture", connection.getImageUrl());
        assertTrue(connection.test());
        TestFacebookApi api = connection.getApi();
        assertNotNull(api);
        assertEquals("123456789", api.getAccessToken());
        assertEquals("123456789", connection.createData().getAccessToken());
        assertEquals("987654321", connection.createData().getRefreshToken());
    }

    private void assertTwitterConnection(Connection<TestTwitterApi> twitter) {
        assertEquals(new ConnectionKey("twitter", "1"), twitter.getKey());
        assertEquals("@kdonald", twitter.getDisplayName());
        assertEquals("http://twitter.com/kdonald", twitter.getProfileUrl());
        assertEquals("http://twitter.com/kdonald/picture", twitter.getImageUrl());
        TestTwitterApi twitterApi = twitter.getApi();
        assertEquals("123456789", twitterApi.getAccessToken());
        assertEquals("987654321", twitterApi.getSecret());
        twitter.sync();
        assertEquals("http://twitter.com/kdonald/a_new_picture", twitter.getImageUrl());
    }

    private void assertFacebookConnection(Connection<TestFacebookApi> facebook) {
        assertEquals(new ConnectionKey("facebook", "9"), facebook.getKey());
        assertEquals(null, facebook.getDisplayName());
        assertEquals(null, facebook.getProfileUrl());
        assertEquals(null, facebook.getImageUrl());
        TestFacebookApi facebookApi = facebook.getApi();
        assertEquals("234567890", facebookApi.getAccessToken());
        facebook.sync();
        assertEquals("Keith Donald", facebook.getDisplayName());
        assertEquals("http://facebook.com/keith.donald", facebook.getProfileUrl());
        assertEquals("http://facebook.com/keith.donald/picture", facebook.getImageUrl());
    }

    // test facebook provider

    private static class TestFacebookConnectionFactory extends OAuth2ConnectionFactory<TestFacebookApi> {

        public TestFacebookConnectionFactory() {
            super("facebook", new TestFacebookServiceProvider(), new TestFacebookApiAdapter());
        }

    }

    private static class TestFacebookServiceProvider implements OAuth2ServiceProvider<TestFacebookApi> {

        public OAuth2Operations getOAuthOperations() {
            return new OAuth2Operations() {
                public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters params) {
                    return null;
                }
                public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters params) {
                    return null;
                }
                public String buildAuthorizeUrl(OAuth2Parameters params) {
                    return null;
                }
                public String buildAuthenticateUrl(OAuth2Parameters params) {
                    return null;
                }
                public AccessGrant exchangeForAccess(String authorizationGrant, String redirectUri, MultiValueMap<String, String> additionalParameters) {
                    return null;
                }
                public AccessGrant exchangeCredentialsForAccess(String username, String password, MultiValueMap<String, String> additionalParameters) {
                    return null;
                }
                public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
                    return new AccessGrant("765432109", "read", "654321098", 3600L);
                }
                @Deprecated
                public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
                    return new AccessGrant("765432109", "read", "654321098", 3600L);
                }
                public AccessGrant authenticateClient() {
                    return null;
                }
                public AccessGrant authenticateClient(String scope) {
                    return null;
                }
            };
        }

        public TestFacebookApi getApi(final String accessToken) {
            return new TestFacebookApi() {
                public String getAccessToken() {
                    return accessToken;
                }
            };
        }

    }

    public interface TestFacebookApi {

        String getAccessToken();

    }

    private static class TestFacebookApiAdapter implements ApiAdapter<TestFacebookApi> {

        private String accountId = "9";

        private String name = "Keith Donald";

        private String profileUrl = "http://facebook.com/keith.donald";

        private String profilePictureUrl = "http://facebook.com/keith.donald/picture";

        public boolean test(TestFacebookApi api) {
            return true;
        }

        public void setConnectionValues(TestFacebookApi api, ConnectionValues values) {
            values.setProviderUserId(accountId);
            values.setDisplayName(name);
            values.setProfileUrl(profileUrl);
            values.setImageUrl(profilePictureUrl);
        }

        public UserProfile fetchUserProfile(TestFacebookApi api) {
            return new UserProfileBuilder().setName(name).setEmail("keith@interface21.com").setUsername("Keith.Donald").build();
        }

        public void updateStatus(TestFacebookApi api, String message) {

        }

    }

    // test twitter provider

    private static class TestTwitterConnectionFactory extends OAuth1ConnectionFactory<TestTwitterApi> {

        public TestTwitterConnectionFactory() {
            super("twitter", new TestTwitterServiceProvider(), new TestTwitterApiAdapter());
        }

    }

    private static class TestTwitterServiceProvider implements OAuth1ServiceProvider<TestTwitterApi> {

        public OAuth1Operations getOAuthOperations() {
            return null;
        }

        public TestTwitterApi getApi(final String accessToken, final String secret) {
            return new TestTwitterApi() {
                public String getAccessToken() {
                    return accessToken;
                }
                public String getSecret() {
                    return secret;
                }
            };
        }

    }

    public interface TestTwitterApi {

        String getAccessToken();

        String getSecret();

    }

    private static class TestTwitterApiAdapter implements ApiAdapter<TestTwitterApi> {

        private String accountId = "1";

        private String name = "@kdonald";

        private String profileUrl = "http://twitter.com/kdonald";

        private String profilePictureUrl = "http://twitter.com/kdonald/a_new_picture";

        public boolean test(TestTwitterApi api) {
            return true;
        }

        public void setConnectionValues(TestTwitterApi api, ConnectionValues values) {
            values.setProviderUserId(accountId);
            values.setDisplayName(name);
            values.setProfileUrl(profileUrl);
            values.setImageUrl(profilePictureUrl);
        }

        public UserProfile fetchUserProfile(TestTwitterApi api) {
            return new UserProfileBuilder().setName(name).setUsername("kdonald").build();
        }

        public void updateStatus(TestTwitterApi api, String message) {
        }

    }

    private static class MySqlCompatibleH2DatabaseConfigurer implements EmbeddedDatabaseConfigurer {
        public void shutdown(DataSource dataSource, String databaseName) {
            try {
                java.sql.Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement();
                stmt.execute("SHUTDOWN");
            }
            catch (SQLException ex) {
            }
        }

        public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
            properties.setDriverClass(Driver.class);
            properties.setUrl(String.format("jdbc:h2:mem:%s;MODE=MYSQL;DB_CLOSE_DELAY=-1", databaseName));
            properties.setUsername("sa");
            properties.setPassword("");
        }
    }
     */
}
