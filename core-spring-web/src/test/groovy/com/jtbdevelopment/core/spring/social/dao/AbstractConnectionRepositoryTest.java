package com.jtbdevelopment.core.spring.social.dao;

import static com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi.FACEBOOK;
import static com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterApi.TWITTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.core.spring.social.dao.utility.FakeConnection;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookConnection;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterApi;
import com.jtbdevelopment.core.spring.social.dao.utility.StringConnectionRepository;
import com.jtbdevelopment.core.spring.social.dao.utility.StringSocialConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Date: 1/3/2015 Time: 12:09 PM
 *
 * Loosely based on spring's own JdbcUsersConnectionRepositoryTest
 */
public class AbstractConnectionRepositoryTest extends ConnectionTestCase {

  private static String TESTID = "TESTID";
  private static StringSocialConnection FACEBOOK1SC = new StringSocialConnection() {{
    setUserId(TESTID);
    setId("L1");
    setProviderId(FACEBOOK);
    setProviderUserId("F1");
    setProfileUrl("F1P");
    setImageUrl("F1I");
    setAccessToken("F1A");
    setSecret("F1S");
    setExpireTime(1L);
    setDisplayName("F1DN");
  }};
  private static StringSocialConnection FACEBOOK2SC = new StringSocialConnection() {{
    setUserId(TESTID);
    setId("L1");
    setProviderId(FACEBOOK);
    setProviderUserId("F2");
    setProfileUrl("F2P");
    setImageUrl("F2I");
    setAccessToken("F2A");
    setSecret("F2S");
    setExpireTime(2L);
    setDisplayName("F2DN");
  }};
  private static StringSocialConnection FACEBOOK2SCDUPE = new StringSocialConnection() {{
    setUserId(TESTID);
    setId("L2DUPE");
    setProviderId(FACEBOOK);
    setProviderUserId("F2");
    setProfileUrl("F2PDUPE");
    setImageUrl("F2IDUPE");
    setAccessToken("F2A");
    setSecret("F2S");
    setExpireTime(2L);
    setDisplayName("F2DNDUPE");
  }};
  private static StringSocialConnection TWITTER1SC = new StringSocialConnection() {{
    setUserId(TESTID);
    setId("L1");
    setProviderId(TWITTER);
    setProviderUserId("T1");
    ;
    setProfileUrl("T1P");
    setImageUrl("T1I");
    setAccessToken("T1A");
    setSecret("T1S");
    setExpireTime(1L);
    setDisplayName("T1DN");
    setRefreshToken("T1RT");
  }};
  private static StringSocialConnection NEWCO1SC = new StringSocialConnection() {{
    setUserId(TESTID);
    setId("L1");
    setProviderId(NEWCO);
    setProviderUserId("N1");
    setProfileUrl("N1P");
    setImageUrl("N1I");
    setAccessToken("N1A");
    setSecret("N1S");
    setExpireTime(1L);
    setDisplayName("N1DN");
  }};
  private AbstractSocialConnectionRepository socialConnectionRepository = Mockito
      .mock(AbstractSocialConnectionRepository.class);
  private StringConnectionRepository repository;

  private static void compareConnectionToSocialConnection(final Connection<?> connection,
      final StringSocialConnection socialConnection) {
    assertEquals(socialConnection.getDisplayName(), connection.getDisplayName());
    assertEquals(socialConnection.getProfileUrl(), connection.getProfileUrl());
    assertEquals(socialConnection.getProviderId(), connection.getKey().getProviderId());
    assertEquals(socialConnection.getProviderUserId(), connection.getKey().getProviderUserId());
    assertEquals(socialConnection.getImageUrl(), connection.getImageUrl());
    assertEquals(socialConnection.getAccessToken(),
        reverse(((FakeConnection) connection).getAccessToken()));
    assertEquals(socialConnection.getRefreshToken(),
        reverse(((FakeConnection) connection).getRefreshToken()));
    assertEquals(socialConnection.getSecret(),
        reverse(((FakeConnection) connection).getSecret()));
    assertEquals(socialConnection.getExpireTime(), ((FakeConnection) connection).getExpireTime());
  }

  private static String reverse(final String input) {
    if (input != null) {
      return new StringBuilder(input).reverse().toString();
    }
    return null;
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    ((Map) ReflectionTestUtils
        .getField(AbstractConnectionRepository.class, "providerConnectionFactoryMap")).clear();
    repository = new StringConnectionRepository(
        socialConnectionRepository,
        connectionFactoryLocator,
        textEncryptor,
        TESTID);
  }

  @Test
  public void testSetupInitializesConnectionRepositoryStaticsFirstTime() {
    Assert.assertEquals(providers, ReflectionTestUtils
        .getField(AbstractConnectionRepository.class, "providerConnectionFactoryMap"));

    connectionFactoryLocator = Mockito.mock(ConnectionFactoryLocator.class);
    when(connectionFactoryLocator.registeredProviderIds()).thenReturn(
        new HashSet<>(Collections.singletonList(FACEBOOK)));
    ConnectionFactory factory = Mockito.mock(ConnectionFactory.class);
    when(connectionFactoryLocator.getConnectionFactory(FACEBOOK)).thenReturn(factory);
    repository = new StringConnectionRepository(socialConnectionRepository,
        connectionFactoryLocator, textEncryptor, TESTID);

    Assert.assertEquals(providers, ReflectionTestUtils
        .getField(AbstractConnectionRepository.class, "providerConnectionFactoryMap"));
  }

  @Test
  public void testSortDefinitions() {
    Assert.assertEquals("providerId: ASC,created: ASC",
        StringConnectionRepository.SORT_PID_CREATED.toString());
    Assert.assertEquals("created: ASC", StringConnectionRepository.SORT_CREATED.toString());
  }

  @Test
  public void testFindAllConnectionsWithValidProviders() {
    when(socialConnectionRepository
        .findByUserId(TESTID, AbstractConnectionRepository.SORT_PID_CREATED)).thenReturn(
        new ArrayList<>(
            Arrays.asList(FACEBOOK1SC, FACEBOOK2SC, TWITTER1SC)));
    MultiValueMap<String, Connection<?>> r = repository.findAllConnections();
    assertEquals(2, r.size());
    assertTrue(r.containsKey(FACEBOOK));
    assertTrue(r.containsKey(TWITTER));
    assertEquals(2, r.get(FACEBOOK).size());
    compareConnectionToSocialConnection(r.get(FACEBOOK).get(0), FACEBOOK1SC);
    compareConnectionToSocialConnection(r.get(FACEBOOK).get(1), FACEBOOK2SC);
    assertEquals(1, r.get(TWITTER).size());
    compareConnectionToSocialConnection(r.get(TWITTER).get(0), TWITTER1SC);
  }

  @Test
  public void testFindAllConnectionsWithEmptyResults() {
    when(socialConnectionRepository
        .findByUserId(TESTID, AbstractConnectionRepository.SORT_PID_CREATED))
        .thenReturn(new ArrayList());
    MultiValueMap<String, Connection<?>> r = repository.findAllConnections();
    assertEquals(2, r.size());
    assertTrue(r.containsKey(FACEBOOK));
    assertTrue(r.containsKey(TWITTER));
    assertTrue(r.get(FACEBOOK).isEmpty());
    assertTrue(r.get(TWITTER).isEmpty());
  }

  @Test(expected = Exception.class)
  public void testFindAllConnectionsWithInvalidProviders() {
    when(socialConnectionRepository
        .findByUserId(TESTID, AbstractConnectionRepository.SORT_PID_CREATED))
        .thenReturn(Arrays.asList(FACEBOOK1SC, FACEBOOK2SC, TWITTER1SC, NEWCO1SC));
    repository.findAllConnections();
  }

  @Test
  public void testFindConnectionsByProviderId() {
    when(socialConnectionRepository
        .findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED))
        .thenReturn(new ArrayList<>(
            Arrays.asList(FACEBOOK2SC, FACEBOOK1SC)));
    List<Connection<?>> connections = repository.findConnections(FACEBOOK);
    assertEquals(2, connections.size());
    compareConnectionToSocialConnection(connections.get(0), FACEBOOK2SC);
    compareConnectionToSocialConnection(connections.get(1), FACEBOOK1SC);
  }

  @Test
  public void testFindConnectionsByProviderIdWithEmptyResults() {
    when(socialConnectionRepository
        .findByUserIdAndProviderId(TESTID, NEWCO, AbstractConnectionRepository.SORT_CREATED))
        .thenReturn(new ArrayList());
    List<Connection<?>> connections = repository.findConnections(NEWCO);
    assertTrue(connections.isEmpty());
  }

  @Test
  public void testFindConnectionsByProviderApi() {
    when(socialConnectionRepository
        .findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED))
        .thenReturn(new ArrayList<>(
            Arrays.asList(FACEBOOK2SC, FACEBOOK1SC)));
    List<Connection<FakeFacebookApi>> connections = repository
        .findConnections(FakeFacebookApi.class);
    assertEquals(2, connections.size());
    compareConnectionToSocialConnection(connections.get(0), FACEBOOK2SC);
    compareConnectionToSocialConnection(connections.get(1), FACEBOOK1SC);
  }

  @Test
  public void testFindConnectionsByProviderApiWithEmptyResults() {
    when(socialConnectionRepository
        .findByUserIdAndProviderId(TESTID, TWITTER, AbstractConnectionRepository.SORT_CREATED))
        .thenReturn(new ArrayList());
    List<Connection<FakeTwitterApi>> connections = repository.findConnections(FakeTwitterApi.class);
    assertTrue(connections.isEmpty());
  }

  @Test
  public void testFindConnectionsToProviderUserIds() {
    List<String> TPIDS = new ArrayList<>(
        Arrays.asList("DONTEXIST", TWITTER1SC.getProviderUserId()));
    List<String> FBPIDS = new ArrayList<>(
        Arrays.asList(FACEBOOK1SC.getProviderUserId(), FACEBOOK2SC.getProviderUserId()));
    MultiValueMap<String, String> input = new LinkedMultiValueMap<>();
    input.put(FACEBOOK, FBPIDS);
    input.put(TWITTER, TPIDS);
    // Tests order of results from DB only important with dupe provider user ids)
    when(socialConnectionRepository
        .findByUserIdAndProviderIdAndProviderUserIdIn(TESTID, FACEBOOK, FBPIDS,
            AbstractConnectionRepository.SORT_PID_CREATED)).thenReturn(
        new ArrayList<>(
            Arrays.asList(FACEBOOK2SC, FACEBOOK1SC, FACEBOOK2SCDUPE)));
    // Tests only one id coming back, plus an irrelevant one
    when(socialConnectionRepository
        .findByUserIdAndProviderIdAndProviderUserIdIn(TESTID, TWITTER, TPIDS,
            AbstractConnectionRepository.SORT_PID_CREATED))
        .thenReturn(new ArrayList<>(Arrays.asList(TWITTER1SC, NEWCO1SC)));
    MultiValueMap<String, Connection<?>> result = repository.findConnectionsToUsers(input);
    assertEquals(2, result.size());
    List<Connection<?>> fb = result.get(FACEBOOK);
    assertEquals(2, fb.size());
    compareConnectionToSocialConnection(fb.get(0), FACEBOOK1SC);
    compareConnectionToSocialConnection(fb.get(1), FACEBOOK2SCDUPE);
    List<Connection<?>> t = result.get(TWITTER);
    assertEquals(2, t.size());
    assertNull(t.get(0));
    compareConnectionToSocialConnection(t.get(1), TWITTER1SC);
  }

  @Test
  public void testFindConnectionsToProviderUserIdsWithNullInput() {
    MultiValueMap<String, Connection<?>> result = repository.findConnectionsToUsers(null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindConnectionsToProviderUserIdsWithEmptyInput() {
    MultiValueMap<String, String> input = new LinkedMultiValueMap<>();
    MultiValueMap<String, Connection<?>> result = repository.findConnectionsToUsers(input);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByConnectionKey() {
    when(socialConnectionRepository
        .findByUserIdAndProviderIdAndProviderUserId(TESTID, TWITTER,
            TWITTER1SC.getProviderUserId())).thenReturn(TWITTER1SC);
    compareConnectionToSocialConnection(
        repository.getConnection(new ConnectionKey(TWITTER, TWITTER1SC.getProviderUserId())),
        TWITTER1SC);
  }

  @Test(expected = NoSuchConnectionException.class)
  public void testFindByConnectionKeyNotFound() {
    when(socialConnectionRepository
        .findByUserIdAndProviderIdAndProviderUserId(TESTID, TWITTER, TWITTER1SC.getProviderId()))
        .thenReturn(null);
    repository.getConnection(new ConnectionKey(TWITTER, TWITTER1SC.getProviderUserId()));
  }

  @Test
  public void testFindByAPIAndPID() {
    when(socialConnectionRepository
        .findByUserIdAndProviderIdAndProviderUserId(TESTID, FACEBOOK,
            FACEBOOK2SC.getProviderUserId())).thenReturn(FACEBOOK2SC);
    compareConnectionToSocialConnection(
        repository.getConnection(FakeFacebookApi.class, FACEBOOK2SC.getProviderUserId()),
        FACEBOOK2SC);
  }

  @Test(expected = NoSuchConnectionException.class)
  public void testFindByAPIAndPIDNotFound() {
    when(socialConnectionRepository
        .findByUserIdAndProviderIdAndProviderUserId(TESTID, FACEBOOK,
            FACEBOOK2SC.getProviderUserId())).thenReturn(null);
    repository.getConnection(FakeFacebookApi.class, FACEBOOK2SC.getProviderUserId());
  }

  @Test
  public void testGetPrimaryConnection() {
    when(socialConnectionRepository
        .findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED))
        .thenReturn(new ArrayList<>(
            Arrays.asList(FACEBOOK2SC, FACEBOOK1SC)));
    compareConnectionToSocialConnection(repository.getPrimaryConnection(FakeFacebookApi.class),
        FACEBOOK2SC);
  }

  @Test(expected = NotConnectedException.class)
  public void testGetPrimaryConnectionNoResults() {
    when(socialConnectionRepository
        .findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED))
        .thenReturn(new ArrayList());
    repository.getPrimaryConnection(FakeFacebookApi.class);
  }

  @Test
  public void testFindPrimaryConnection() {
    when(socialConnectionRepository
        .findByUserIdAndProviderId(TESTID, FACEBOOK, AbstractConnectionRepository.SORT_CREATED))
        .thenReturn(new ArrayList<>(
            Arrays.asList(FACEBOOK2SC, FACEBOOK1SC)));
    compareConnectionToSocialConnection(repository.findPrimaryConnection(FakeFacebookApi.class),
        FACEBOOK2SC);
  }

  @Test
  public void testFindPrimaryConnectionNoResults() {
    when(socialConnectionRepository
        .findByUserIdAndProviderId(TESTID, FACEBOOK, StringConnectionRepository.SORT_CREATED))
        .thenReturn(new ArrayList());
    assertNull(repository.findPrimaryConnection(FakeFacebookApi.class));
  }

  @Test
  public void testRemoveConnections() {
    when(socialConnectionRepository.deleteByUserIdAndProviderId(TESTID, TWITTER))
        .thenReturn(1L);
    repository.removeConnections(TWITTER);
    verify(socialConnectionRepository).deleteByUserIdAndProviderId(TESTID, TWITTER);
  }

  @Test
  public void testRemoveConnection() {
    when(socialConnectionRepository
        .deleteByUserIdAndProviderIdAndProviderUserId(TESTID, TWITTER,
            TWITTER1SC.getProviderUserId())).thenReturn(1L);
    repository.removeConnection(new ConnectionKey(TWITTER, TWITTER1SC.getProviderUserId()));
    verify(socialConnectionRepository)
        .deleteByUserIdAndProviderIdAndProviderUserId(TESTID, TWITTER,
            TWITTER1SC.getProviderUserId());
  }

  @Test
  public void testAddConnection() {
    when(socialConnectionRepository.save(Matchers.isA(SocialConnection.class)))
        .then(invocation -> {
          SocialConnection sc = (SocialConnection) invocation.getArguments()[0];
          assertNull(sc.getId());
          assertEquals(FACEBOOK, sc.getProviderId());
          assertEquals(TESTID, sc.getUserId());
          assertEquals(FACEBOOK1SC.getProviderUserId(), sc.getProviderUserId());
          assertEquals(FACEBOOK1SC.getImageUrl(), sc.getImageUrl());
          assertEquals(FACEBOOK1SC.getProfileUrl(), sc.getProfileUrl());
          assertEquals(FACEBOOK1SC.getDisplayName(), sc.getDisplayName());
          assertEquals(FACEBOOK1SC.getExpireTime(), sc.getExpireTime());
          assertEquals(FACEBOOK1SC.getAccessToken(), reverse(sc.getAccessToken()));
          assertEquals(FACEBOOK1SC.getRefreshToken(), reverse(sc.getRefreshToken()));
          assertEquals(FACEBOOK1SC.getSecret(), reverse(sc.getSecret()));
          return sc;
        });
    repository.addConnection(new FakeFacebookConnection(
        new ConnectionData(FACEBOOK, FACEBOOK1SC.getProviderUserId(), FACEBOOK1SC.getDisplayName(),
            FACEBOOK1SC.getProfileUrl(), FACEBOOK1SC.getImageUrl(), FACEBOOK1SC.getAccessToken(),
            FACEBOOK1SC.getSecret(), FACEBOOK1SC.getRefreshToken(), FACEBOOK1SC.getExpireTime())));
    verify(socialConnectionRepository).save(Matchers.isA(SocialConnection.class));
  }

  @Test(expected = DuplicateConnectionException.class)
  public void testAddConnectionDuplicate() {
    when(socialConnectionRepository.save(Matchers.isA(SocialConnection.class)))
        .thenThrow(new DuplicateKeyException("dupe"));

    repository.addConnection(new FakeFacebookConnection(
        new ConnectionData(FACEBOOK, FACEBOOK1SC.getProviderUserId(),
            FACEBOOK1SC.getDisplayName(), FACEBOOK1SC.getProfileUrl(),
            FACEBOOK1SC.getImageUrl(), FACEBOOK1SC.getAccessToken(), FACEBOOK1SC.getSecret(),
            FACEBOOK1SC.getRefreshToken(), FACEBOOK1SC.getExpireTime())));
  }

  @Test
  public void testUpdateConnection() {
    final String newProfile = "newprofile";
    final String newImage = "newimage";
    final String newRefreshToken = "newrefresh";
    final String newSecret = "newsecret";
    final String newAccessToken = "na";
    final String newDisplayName = "newdisplay";
    final Long newExpire = 151L;
    when(socialConnectionRepository
        .findByUserIdAndProviderIdAndProviderUserId(TESTID, FACEBOOK,
            FACEBOOK2SC.getProviderUserId())).thenReturn(FACEBOOK2SC);
    when(socialConnectionRepository.save(Matchers.isA(SocialConnection.class)))
        .then(invocation -> {
          SocialConnection sc = (SocialConnection) invocation.getArguments()[0];
          assertEquals(FACEBOOK2SC.getId(), sc.getId());
          assertEquals(FACEBOOK2SC.getProviderUserId(), sc.getProviderUserId());
          assertEquals(FACEBOOK2SC.getProfileUrl(), sc.getProfileUrl());
          assertEquals(FACEBOOK, sc.getProviderId());
          assertEquals(newImage, sc.getImageUrl());
          assertEquals(newProfile, sc.getProfileUrl());
          assertEquals(newDisplayName, sc.getDisplayName());
          assertEquals(newExpire, sc.getExpireTime());
          assertEquals(newAccessToken, reverse(sc.getAccessToken()));
          assertEquals(newSecret, reverse(sc.getSecret()));
          assertEquals(newRefreshToken, reverse(sc.getRefreshToken()));
          assertEquals(TESTID, sc.getUserId());
          return sc;
        });
    repository.updateConnection(new FakeFacebookConnection(
        new ConnectionData(FACEBOOK, FACEBOOK2SC.getProviderUserId(), newDisplayName, newProfile,
            newImage, newAccessToken, newSecret, newRefreshToken, newExpire)));
    verify(socialConnectionRepository).save(Matchers.isA(StringSocialConnection.class));
  }
}
