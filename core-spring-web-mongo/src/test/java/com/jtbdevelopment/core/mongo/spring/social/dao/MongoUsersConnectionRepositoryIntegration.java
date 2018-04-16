package com.jtbdevelopment.core.mongo.spring.social.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.core.mongo.spring.AbstractCoreMongoConfiguration;
import com.jtbdevelopment.core.mongo.spring.AbstractMongoDefaultSpringContextIntegration;
import com.jtbdevelopment.core.mongo.spring.MongoProperties;
import com.jtbdevelopment.core.mongo.spring.converters.InstantToStringConverter;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeConnection;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeConnectionFactory;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookConnectionFactory;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterApi;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterConnectionFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Date: 1/4/2015 Time: 8:19 PM
 *
 * Significantly derived from spring's JdbcUsersConnectionRepositoryTest
 */
public class MongoUsersConnectionRepositoryIntegration extends
    AbstractMongoDefaultSpringContextIntegration {

  private static final String CREATED_COLUMN = "created";
  private static final String USERID_COLUMN = "userId";
  private static final String PROVIDERID_COLUMN = "providerId";
  private static final String PROVIDERUSERID_COLUMN = "providerUserId";
  private static final String DISPLAYNAME_COLUMN = "displayName";
  private static final String PROFILE_COLUMN = "profileUrl";
  private static final String IMAGE_COLUMN = "imageUrl";
  private static final String TOKEN_COLUMN = "accessToken";
  private static final String SECRET_COLUMN = "secret";
  private static final String REFRESH_COLUMN = "refreshToken";
  private static final String EXPIRE_COLUMN = "expireTime";
  private static HashMap<String, Object> USER1_TWITTER1;
  private static HashMap<String, Object> USER1_FACEBOOK9;
  private static HashMap<String, Object> USER1_FACEBOOK10;
  private static HashMap<String, Object> USER2_FACEBOOK11;
  private static HashMap<String, Object> USER2_FACEBOOK9DUPE;
  private static HashMap<String, Object> USER1_NEWCO1;
  private Map<String, FakeConnectionFactory> providers;
  private TextEncryptor textEncryptor;
  private AbstractUsersConnectionRepository socialConnectionRepository;
  private ConnectionRepository user1SocialConnectionRepository;
  private InstantToStringConverter instantToStringConverter;
  private MongoCollection collection;

  {
    HashMap<String, Object> map = new HashMap<>(11);
    map.put(USERID_COLUMN, "1");
    map.put(PROVIDERID_COLUMN, FakeTwitterApi.TWITTER);
    map.put(PROVIDERUSERID_COLUMN, "1");
    map.put(CREATED_COLUMN, Instant.now());
    map.put(DISPLAYNAME_COLUMN, "@kdonald");
    map.put(PROFILE_COLUMN, "http://twitter.com/kdonald");
    map.put(IMAGE_COLUMN, "http://twitter.com/kdonald/picture");
    map.put(TOKEN_COLUMN, "123456789");
    map.put(SECRET_COLUMN, "987654321");
    map.put(REFRESH_COLUMN, null);
    map.put(EXPIRE_COLUMN, null);
    USER1_TWITTER1 = map;
  }

  {
    HashMap<String, Object> map = new HashMap<>(
        11);
    map.put(USERID_COLUMN, "1");
    map.put(PROVIDERID_COLUMN, FakeFacebookApi.FACEBOOK);
    map.put(PROVIDERUSERID_COLUMN, "9");
    map.put(CREATED_COLUMN, Instant.now());
    map.put(DISPLAYNAME_COLUMN, null);
    map.put(PROFILE_COLUMN, null);
    map.put(IMAGE_COLUMN, null);
    map.put(TOKEN_COLUMN, "234567890");
    map.put(SECRET_COLUMN, null);
    map.put(REFRESH_COLUMN, "345678901");
    map.put(EXPIRE_COLUMN, System.currentTimeMillis() + 3600000);
    try {
      //  need a gap between this one and 10
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    USER1_FACEBOOK9 = map;
  }

  {
    HashMap<String, Object> map = new HashMap<>(
        11);
    map.put(USERID_COLUMN, "1");
    map.put(PROVIDERID_COLUMN, FakeFacebookApi.FACEBOOK);
    map.put(PROVIDERUSERID_COLUMN, "10");
    map.put(CREATED_COLUMN, Instant.now());
    map.put(DISPLAYNAME_COLUMN, null);
    map.put(PROFILE_COLUMN, null);
    map.put(IMAGE_COLUMN, null);
    map.put(TOKEN_COLUMN, "456789012");
    map.put(SECRET_COLUMN, null);
    map.put(REFRESH_COLUMN, "56789012");
    map.put(EXPIRE_COLUMN, System.currentTimeMillis() + 3600000);
    USER1_FACEBOOK10 = map;
  }

  {
    HashMap<String, Object> map = new HashMap<>(
        11);
    map.put(USERID_COLUMN, "2");
    map.put(PROVIDERID_COLUMN, FakeFacebookApi.FACEBOOK);
    map.put(PROVIDERUSERID_COLUMN, "11");
    map.put(CREATED_COLUMN, Instant.now());
    map.put(DISPLAYNAME_COLUMN, null);
    map.put(PROFILE_COLUMN, null);
    map.put(IMAGE_COLUMN, null);
    map.put(TOKEN_COLUMN, "456789012");
    map.put(SECRET_COLUMN, null);
    map.put(REFRESH_COLUMN, "56789012");
    map.put(EXPIRE_COLUMN, System.currentTimeMillis() + 3600000);
    USER2_FACEBOOK11 = map;
  }

  {
    HashMap<String, Object> map = new HashMap<>(
        11);
    map.put(USERID_COLUMN, "2");
    map.put(PROVIDERID_COLUMN, FakeFacebookApi.FACEBOOK);
    map.put(PROVIDERUSERID_COLUMN, "9");
    map.put(CREATED_COLUMN, Instant.now());
    map.put(DISPLAYNAME_COLUMN, null);
    map.put(PROFILE_COLUMN, null);
    map.put(IMAGE_COLUMN, null);
    map.put(TOKEN_COLUMN, "234567890");
    map.put(SECRET_COLUMN, null);
    map.put(REFRESH_COLUMN, "345678901");
    map.put(EXPIRE_COLUMN, System.currentTimeMillis() + 3600000);
    USER2_FACEBOOK9DUPE = map;
  }

  {
    HashMap<String, Object> map = new HashMap<>(
        11);
    map.put(USERID_COLUMN, "1");
    map.put(PROVIDERID_COLUMN, "NEWCONNECTION");
    map.put(PROVIDERUSERID_COLUMN, "1");
    map.put(CREATED_COLUMN, Instant.now());
    map.put(DISPLAYNAME_COLUMN, "@kdonald");
    map.put(PROFILE_COLUMN, "http://twitter.com/kdonald");
    map.put(IMAGE_COLUMN, "http://twitter.com/kdonald/picture");
    map.put(TOKEN_COLUMN, "123456789");
    map.put(SECRET_COLUMN, "987654321");
    map.put(REFRESH_COLUMN, null);
    map.put(EXPIRE_COLUMN, null);
    USER1_NEWCO1 = map;
  }

  @Before
  public void setUp() {
    textEncryptor = context.getBean(TextEncryptor.class);
    HashMap<String, FakeConnectionFactory> map = new HashMap<>();
    map.put(FakeFacebookApi.FACEBOOK, new FakeFacebookConnectionFactory());
    map.put(FakeTwitterApi.TWITTER, new FakeTwitterConnectionFactory());
    providers = map;

    collection = db.getCollection("socialConnections");
    collection.deleteMany(new Document());
    assertEquals(0, collection.count());

    socialConnectionRepository = context.getBean(AbstractUsersConnectionRepository.class);
    user1SocialConnectionRepository = socialConnectionRepository.createConnectionRepository("1");
    instantToStringConverter = context.getBean(InstantToStringConverter.class);
  }

  @Test
  public void testCollectionConfiguration() {
    final boolean[] foundIndices = {false, false};
    collection.listIndexes().forEach((Consumer<Document>) document -> {
      String switchArg = (String) document.get("name");
      switch (switchArg) {
        case "sc_uidpidc": {
          foundIndices[0] = true;
          assert document.get("unique").equals(Boolean.TRUE);
          Document key = (Document) document.get("key");
          assert key.size() == 3;
          Iterator iterator = key.entrySet().iterator();
          Entry<Object, Object> sub = (Entry<Object, Object>) iterator.next();
          assert sub.getValue().equals(1);
          assert sub.getKey().equals("userId");
          sub = ((Entry<Object, Object>) (iterator.next()));
          assert sub.getValue().equals(1);
          assert sub.getKey().equals("providerId");
          sub = ((Entry<Object, Object>) (iterator.next()));
          assert sub.getValue().equals(1);
          assert sub.getKey().equals("created");
          assert !iterator.hasNext();
        }
        break;
        case "sc_pk": {
          foundIndices[1] = true;
          assertTrue((boolean) document.get("unique"));
          Document key = (Document) document.get("key");
          assertEquals(3, key.size());

          Iterator iterator = key.entrySet().iterator();
          Entry<Object, Object> sub = (Entry<Object, Object>) iterator.next();
          assert sub.getValue().equals(1);
          assert sub.getKey().equals("userId");
          sub = ((Entry<Object, Object>) (iterator.next()));
          assert sub.getValue().equals(1);
          assert sub.getKey().equals("providerId");
          sub = ((Entry<Object, Object>) (iterator.next()));
          assert sub.getValue().equals(1);
          assert sub.getKey().equals("providerUserId");
          assert !iterator.hasNext();
        }
        break;
      }
    });
    assertTrue(foundIndices[0]);
    assertTrue(foundIndices[1]);
  }

  @Test
  public void testFindUserIdWithConnection() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    List<String> userIds = socialConnectionRepository.findUserIdsWithConnection(
        user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class));
    assertEquals("1", userIds.get(0));
  }

  @Test
  public void testFindUserIdWithConnectionNoSuchConnection() {
    HashMap<String, String> map = new HashMap<>(2);
    map.put(PROVIDERUSERID_COLUMN, "12345");
    map.put(PROVIDERID_COLUMN, FakeFacebookApi.FACEBOOK);
    Connection<?> connection = providers.get(FakeFacebookApi.FACEBOOK)
        .createConnection(createConnectionData(map));
    assertEquals(0, socialConnectionRepository.findUserIdsWithConnection(connection).size());
  }

  @Test
  public void testFindUserIdWithConnectionMultipleConnectionsToSameProviderUser() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER2_FACEBOOK9DUPE);
    List<String> localUserIds = socialConnectionRepository.findUserIdsWithConnection(
        user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class));
    assertEquals(2, localUserIds.size());
    assertEquals("1", localUserIds.get(0));
    assertEquals("2", localUserIds.get(1));
  }

  @Test
  public void testFindUserIdWithConnectionNoConnectionWithWorkingConnectionSignUp() {
    HashMap<String, String> map = new HashMap<>(2);
    map.put(PROVIDERUSERID_COLUMN, "12345");
    map.put(PROVIDERID_COLUMN, FakeFacebookApi.FACEBOOK);
    Connection<?> connection = providers.get(FakeFacebookApi.FACEBOOK)
        .createConnection(createConnectionData(map));
    try {
      ReflectionTestUtils.setField(
          socialConnectionRepository,
          "connectionSignUp",
          (ConnectionSignUp) c -> "batman");
      List<String> userIds = socialConnectionRepository.findUserIdsWithConnection(connection);
      assertEquals(1, userIds.size());
      assertEquals("batman", userIds.get(0));
    } finally {
      ReflectionTestUtils.setField(socialConnectionRepository, "connectionSignUp", null);
    }

  }

  @Test
  public void testFindUserIdWithConnectionNoConnectionWithConnectionSignUpReturningNull() {
    HashMap<String, String> map = new HashMap<>(2);
    map.put(PROVIDERUSERID_COLUMN, "12345");
    map.put(PROVIDERID_COLUMN, FakeFacebookApi.FACEBOOK);
    Connection<?> connection = providers.get(FakeFacebookApi.FACEBOOK)
        .createConnection(createConnectionData(map));
    try {
      ReflectionTestUtils.setField(socialConnectionRepository,
          "connectionSignUp",
          (ConnectionSignUp) c -> null);
      List<String> userIds = socialConnectionRepository.findUserIdsWithConnection(connection);
      assertEquals(0, userIds.size());
    } finally {
      ReflectionTestUtils.setField(socialConnectionRepository, "connectionSignUp", null);
    }

  }

  @Test
  public void testFindUserIdsConnectedTo() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER2_FACEBOOK11);
    Set<String> localUserIds = socialConnectionRepository
        .findUserIdsConnectedTo("facebook", new HashSet<>(Arrays.asList("9", "11")));
    assertEquals(2, localUserIds.size());
    assertTrue(localUserIds.contains("1"));
    assertTrue(localUserIds.contains("2"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllConnections() {
    insertSocialConnectionRow(USER1_TWITTER1);
    insertSocialConnectionRow(USER1_FACEBOOK9);
    MultiValueMap<String, Connection<?>> connections = user1SocialConnectionRepository
        .findAllConnections();
    assertEquals(2, connections.size());
    Connection<FakeFacebookApi> facebook = (Connection<FakeFacebookApi>) connections
        .getFirst(FakeFacebookApi.FACEBOOK);
    compareConnectionToMap(facebook, USER1_FACEBOOK9);
    Connection<FakeTwitterApi> twitter = (Connection<FakeTwitterApi>) connections
        .getFirst(FakeTwitterApi.TWITTER);
    compareConnectionToMap(twitter, USER1_TWITTER1);
  }

  @Test
  public void testFindAllConnectionsMultipleConnectionResults() {
    insertSocialConnectionRow(USER1_TWITTER1);
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER1_FACEBOOK10);
    MultiValueMap<String, Connection<?>> connections = user1SocialConnectionRepository
        .findAllConnections();
    assertEquals(2, connections.size());
    assertEquals(2, connections.get(FakeFacebookApi.FACEBOOK).size());
    assertEquals(1, connections.get(FakeTwitterApi.TWITTER).size());
  }

  @Test
  public void testFindAllConnectionsEmptyResult() {
    MultiValueMap<String, Connection<?>> connections = user1SocialConnectionRepository
        .findAllConnections();
    assertEquals(2, connections.size());
    assertEquals(0, connections.get(FakeFacebookApi.FACEBOOK).size());
    assertEquals(0, connections.get(FakeTwitterApi.TWITTER).size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoSuchConnectionFactory() {
    insertSocialConnectionRow(USER1_NEWCO1);
    user1SocialConnectionRepository.findAllConnections();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindConnectionsByProviderId() {
    insertSocialConnectionRow(USER1_TWITTER1);
    List<Connection<?>> connections = user1SocialConnectionRepository
        .findConnections(FakeTwitterApi.TWITTER);
    assertEquals(1, connections.size());
    compareConnectionToMap(connections.get(0), USER1_TWITTER1);
  }

  @Test
  public void testFindConnectionsByProviderIdEmptyResult() {
    assert user1SocialConnectionRepository.findConnections(FakeFacebookApi.FACEBOOK).isEmpty();
  }

  @Test
  public void testFindConnectionsByApi() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER1_FACEBOOK10);
    List<Connection<FakeFacebookApi>> connections = user1SocialConnectionRepository
        .findConnections(FakeFacebookApi.class);
    assertEquals(2, connections.size());
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
    insertSocialConnectionRow(USER1_TWITTER1);
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER1_FACEBOOK10);

    MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<>();
    providerUsers.add(FakeFacebookApi.FACEBOOK, "10");
    providerUsers.add(FakeFacebookApi.FACEBOOK, "9");
    providerUsers.add(FakeTwitterApi.TWITTER, "1");
    MultiValueMap<String, Connection<?>> connectionsForUsers = user1SocialConnectionRepository
        .findConnectionsToUsers(providerUsers);
    assertEquals(2, connectionsForUsers.size());
    assertEquals("10",
        connectionsForUsers.getFirst(FakeFacebookApi.FACEBOOK).getKey().getProviderUserId());
    compareConnectionToMap(connectionsForUsers.get(FakeFacebookApi.FACEBOOK).get(1),
        USER1_FACEBOOK9);
    compareConnectionToMap(connectionsForUsers.get(FakeTwitterApi.TWITTER).get(0), USER1_TWITTER1);
  }

  @Test
  public void testFindConnectionsToUsersEmptyResult() {
    MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<>();
    providerUsers.add(FakeFacebookApi.FACEBOOK, "1");

    MultiValueMap<String, Connection<?>> users = user1SocialConnectionRepository
        .findConnectionsToUsers(providerUsers);
    assertEquals(1, users.size());
    assertEquals(1, users.get(FakeFacebookApi.FACEBOOK).size());
    assertNull(users.get(FakeFacebookApi.FACEBOOK).get(0));
  }

  @Test
  public void testFindConnectionsToUsersNullInput() {
    assertTrue(user1SocialConnectionRepository.findConnectionsToUsers(null).isEmpty());
  }

  @Test
  public void testFindConnectionsToUsersNEmptyInput() {
    MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<>();
    assertTrue(user1SocialConnectionRepository.findConnectionsToUsers(providerUsers).isEmpty());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindConnectionByKey() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    compareConnectionToMap(user1SocialConnectionRepository
        .getConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "9")), USER1_FACEBOOK9);
  }

  @Test(expected = NoSuchConnectionException.class)
  public void testFindConnectionByKeyNoSuchConnection() {
    user1SocialConnectionRepository
        .getConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "bogus"));
  }

  @Test
  public void findConnectionByApiToUser() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER1_FACEBOOK10);
    compareConnectionToMap(
        user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "9"), USER1_FACEBOOK9);
    assertEquals("10",
        user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "10").getKey()
            .getProviderUserId());
  }

  @Test(expected = NoSuchConnectionException.class)
  public void testFindConnectionByApiToUserNoSuchConnection() {
    user1SocialConnectionRepository.getConnection(FakeFacebookApi.class, "9");
  }

  @Test
  public void testFindPrimaryConnection() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    compareConnectionToMap(
        user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class),
        USER1_FACEBOOK9);
  }

  @Test
  public void testRemoveConnections() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER1_FACEBOOK10);
    insertSocialConnectionRow(USER1_TWITTER1);//  More rigorous
    Bson query = Filters.eq(USERID_COLUMN, "1");
    assertEquals(3, collection.count(query));
    user1SocialConnectionRepository.removeConnections(FakeFacebookApi.FACEBOOK);
    assertEquals(1, collection.count(query));
  }

  @Test
  public void testRemoveConnectionsToProviderNoOp() {
    insertSocialConnectionRow(USER1_FACEBOOK9);//  More rigorous
    assertEquals(1, collection.count());
    user1SocialConnectionRepository.removeConnections(FakeTwitterApi.TWITTER);
    assertEquals(1, collection.count());
  }

  @Test
  public void testRemoveConnection() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER1_FACEBOOK10);// More rigorous test
    Bson query = Filters.eq(USERID_COLUMN, "1");
    assertEquals(2, collection.count(query));
    user1SocialConnectionRepository
        .removeConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "9"));
    assertEquals(1, collection.count(query));
  }

  @Test
  public void testRemoveConnectionNoOp() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    assertEquals(1, collection.count());
    user1SocialConnectionRepository
        .removeConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "1"));
    assertEquals(1, collection.count());
  }

  @Test
  public void testFindPrimaryConnectionSelectFromMultipleByCreationTime() {
    insertSocialConnectionRow(USER1_FACEBOOK10);
    insertSocialConnectionRow(USER1_FACEBOOK9);
    compareConnectionToMap(
        user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class),
        USER1_FACEBOOK9);
  }

  @Test(expected = NotConnectedException.class)
  public void testFindPrimaryConnectionNotConnected() {
    user1SocialConnectionRepository.getPrimaryConnection(FakeFacebookApi.class);
  }

  @Test
  public void testAddConnection() {
    Connection<?> connection = providers.get(FakeFacebookApi.FACEBOOK)
        .createConnection(createConnectionData(USER1_FACEBOOK10));
    user1SocialConnectionRepository.addConnection(connection);
    Connection<?> loaded = user1SocialConnectionRepository
        .getPrimaryConnection(FakeFacebookApi.class);
    compareConnectionToMap(loaded, USER1_FACEBOOK10);
  }

  @Test(expected = DuplicateConnectionException.class)
  public void testAddConnectionDuplicate() {
    Connection<?> connection = providers.get(FakeFacebookApi.FACEBOOK)
        .createConnection(createConnectionData(USER1_FACEBOOK10));
    user1SocialConnectionRepository.addConnection(connection);
    user1SocialConnectionRepository.addConnection(connection);
  }

  @Test
  public void testUpdateConnectionProfileFields() {
    insertSocialConnectionRow(USER1_TWITTER1);
    Connection<FakeTwitterApi> twitter = user1SocialConnectionRepository
        .getPrimaryConnection(FakeTwitterApi.class);
    compareConnectionToMap(twitter, USER1_TWITTER1);
    Map<String, Object> newValues = new HashMap<>();
    newValues.putAll(USER1_TWITTER1);
    newValues.put(PROFILE_COLUMN, "http://twitter.com/kdonald/a_new_picture");
    twitter = providers.get(FakeFacebookApi.FACEBOOK)
        .createConnection(createConnectionData(newValues));
    user1SocialConnectionRepository.updateConnection(twitter);
    Connection<FakeTwitterApi> twitter2 = user1SocialConnectionRepository
        .getPrimaryConnection(FakeTwitterApi.class);
    compareConnectionToMap(twitter2, newValues);
  }

  @Test
  public void testUpdateConnectionAccessFields() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    Connection<FakeFacebookApi> facebook = user1SocialConnectionRepository
        .getPrimaryConnection(FakeFacebookApi.class);
    compareConnectionToMap(facebook, USER1_FACEBOOK9);
    Map<String, Object> newValues = new HashMap<>();
    newValues.putAll(USER1_FACEBOOK9);
    String newToken = "765432109";
    String newRefresh = "654321098";
    newValues.put(TOKEN_COLUMN, newToken);
    newValues.put(REFRESH_COLUMN, newRefresh);
    facebook = providers.get(FakeFacebookApi.FACEBOOK)
        .createConnection(createConnectionData(newValues));
    user1SocialConnectionRepository.updateConnection(facebook);
    Connection<FakeFacebookApi> facebook2 = user1SocialConnectionRepository
        .getPrimaryConnection(FakeFacebookApi.class);
    ConnectionData data = facebook2.createData();
    assertEquals(newToken, data.getAccessToken());
    assertEquals(newRefresh, data.getRefreshToken());
  }

  @Test
  public void testFindPrimaryConnectionAfterRemove() {
    insertSocialConnectionRow(USER1_FACEBOOK9);
    insertSocialConnectionRow(USER1_FACEBOOK10);
    // 9 is the providerUserId of the first Facebook connection
    user1SocialConnectionRepository
        .removeConnection(new ConnectionKey(FakeFacebookApi.FACEBOOK, "9"));
    assertEquals(1, user1SocialConnectionRepository.findConnections(FakeFacebookApi.class).size());
    compareConnectionToMap(
        user1SocialConnectionRepository.findPrimaryConnection(FakeFacebookApi.class),
        USER1_FACEBOOK10);
  }

  private void compareConnectionToMap(final Connection<?> connection, final Map values) {
    assertTrue(connection instanceof FakeConnection);
    assertEquals(values.get(DISPLAYNAME_COLUMN), connection.getDisplayName());
    assertEquals(values.get(PROFILE_COLUMN), connection.getProfileUrl());
    assertEquals(values.get(PROVIDERID_COLUMN), connection.getKey().getProviderId());
    assertEquals(values.get(PROVIDERUSERID_COLUMN), connection.getKey().getProviderUserId());
    assertEquals(values.get(IMAGE_COLUMN), connection.getImageUrl());
    FakeConnection fakeConnection = (FakeConnection) connection;
    assertEquals(values.get(TOKEN_COLUMN), fakeConnection.getAccessToken());
    assertEquals(values.get(REFRESH_COLUMN), fakeConnection.getRefreshToken());
    assertEquals(values.get(SECRET_COLUMN), fakeConnection.getSecret());
    assertEquals(values.get(EXPIRE_COLUMN), fakeConnection.getExpireTime());
  }

  private ConnectionData createConnectionData(final Map values) {
    return new ConnectionData(
        (String) values.get(PROVIDERID_COLUMN),
        (String) values.get(PROVIDERUSERID_COLUMN),
        (String) values.get(DISPLAYNAME_COLUMN),
        (String) values.get(PROFILE_COLUMN),
        (String) values.get(IMAGE_COLUMN),
        (String) values.get(TOKEN_COLUMN),
        (String) values.get(SECRET_COLUMN),
        (String) values.get(REFRESH_COLUMN),
        (Long) values.get(EXPIRE_COLUMN));
  }

  private void insertSocialConnectionRow(final Map<String, Object> values) {
    Map<String, Object> newValues = new HashMap<>();
    newValues.putAll(values);
    newValues.put(CREATED_COLUMN,
        instantToStringConverter.convert((Instant) values.get(CREATED_COLUMN)));
    newValues.put(TOKEN_COLUMN,
        values.get(TOKEN_COLUMN) != null ? textEncryptor
            .encrypt(values.get(TOKEN_COLUMN).toString()) : null);
    newValues.put(REFRESH_COLUMN,
        values.get(REFRESH_COLUMN) != null ? textEncryptor
            .encrypt(values.get(REFRESH_COLUMN).toString()) : null);
    newValues.put(SECRET_COLUMN,
        values.get(SECRET_COLUMN) != null ? textEncryptor
            .encrypt(values.get(SECRET_COLUMN).toString()) : null);
    final Document document = new Document();
    document.putAll(newValues);
    collection.insertOne(document);
  }

  @Configuration
  private static class IntegrationSocialConfiguration extends AbstractCoreMongoConfiguration {

    public IntegrationSocialConfiguration(final List<MongoConverter> mongoConverters,
        final MongoProperties mongoProperties) {
      super(mongoConverters, mongoProperties);
    }

    @Bean
    @Autowired
    public ConnectionFactoryRegistry connectionFactoryLocator() {
      ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
      registry.addConnectionFactory(new FakeFacebookConnectionFactory());
      registry.addConnectionFactory(new FakeTwitterConnectionFactory());
      return registry;
    }

  }
}
