package com.jtbdevelopment.core.spring.social.dao;

import static com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi.FACEBOOK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.core.spring.social.dao.utility.StringSocialConnection;
import com.jtbdevelopment.core.spring.social.dao.utility.StringUsersConnectionRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;

/**
 * Date: 1/2/15 Time: 6:37 PM
 *
 * loosely based on spring's own JdbcUsersConnectionRepositoryTest
 */
public class AbstractUsersConnectionRepositoryTest extends ConnectionTestCase {

  private StringUsersConnectionRepository repository;

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testFindValidUserIdWithValidConnectionFactory() {
    String providedUserId = "1234";
    Connection connection = mock(Connection.class);
    when(connection.getKey()).thenReturn(new ConnectionKey(FACEBOOK, providedUserId));
    AbstractSocialConnectionRepository socialConnectionRepository =
        mock(AbstractSocialConnectionRepository.class);

    when(socialConnectionRepository.findByProviderIdAndProviderUserId(FACEBOOK, providedUserId))
        .thenReturn(Arrays.asList(
            new StringSocialConnection() {{
              setProviderId(FACEBOOK);
              setProviderUserId(providedUserId);
              setUserId("1");
            }},
            new StringSocialConnection() {{
              setProviderId(FACEBOOK);
              setProviderUserId(providedUserId);
              setUserId("2");
            }}
        ));
    repository = new StringUsersConnectionRepository(null, socialConnectionRepository,
        textEncryptor, connectionFactoryLocator);
    assertEquals(
        Arrays.asList("1", "2"),
        repository.findUserIdsWithConnection(connection));
  }

  @Test
  public void testFindInValidUserIdWithSuccessfulSignUp() {
    String providedUserId = "1234";
    final String localUserId = "1";
    final ConnectionData connectionData = new ConnectionData(FACEBOOK, providedUserId, "display",
        "profile", "image", "at", "s", "rt", 100L);
    final Instant now = Instant.now();
    Connection connection = mock(Connection.class);
    when(connection.getKey()).thenReturn(new ConnectionKey(FACEBOOK, providedUserId));
    when(connection.createData()).thenReturn(connectionData);
    AbstractSocialConnectionRepository socialConnectionRepository =
        mock(AbstractSocialConnectionRepository.class);
    when(socialConnectionRepository.findByProviderIdAndProviderUserId(FACEBOOK, providedUserId))
        .thenReturn(new ArrayList());
    when(socialConnectionRepository.save(isA(AbstractSocialConnection.class)))
        .then(invocation -> {
          AbstractSocialConnection sc = (AbstractSocialConnection) invocation.getArguments()[0];
          assertNull(sc.getId());
          assertEquals(localUserId, sc.getUserId());
          assertEquals(connectionData.getProviderUserId(), sc.getProviderUserId());
          assertTrue(now.compareTo(sc.getCreated()) <= 0);
          assertEquals(connectionData.getDisplayName(), sc.getDisplayName());
          assertEquals(connectionData.getExpireTime(), sc.getExpireTime());
          assertEquals(connectionData.getImageUrl(), sc.getImageUrl());
          assertEquals(connectionData.getProfileUrl(), sc.getProfileUrl());
          assertEquals(connectionData.getProviderId(), sc.getProviderId());
          assertEquals(StringGroovyMethods.reverse(connectionData.getAccessToken()),
              sc.getAccessToken());
          assertEquals(StringGroovyMethods.reverse(connectionData.getRefreshToken()),
              sc.getRefreshToken());
          assertEquals(StringGroovyMethods.reverse(connectionData.getSecret()),
              sc.getSecret());
          return sc;
        });
    ConnectionSignUp signUp = mock(ConnectionSignUp.class);
    when(signUp.execute(connection)).thenReturn(localUserId);

    repository = new StringUsersConnectionRepository(signUp, socialConnectionRepository,
        textEncryptor, connectionFactoryLocator);

    assertEquals(
        Collections.singletonList(localUserId),
        repository.findUserIdsWithConnection(connection));
    verify(socialConnectionRepository).save(isA(AbstractSocialConnection.class));
  }

  @Test
  public void testFindInValidUserIdWithFailedSignUp() {
    String providerUserId = "1234";
    Connection connection = mock(Connection.class);
    when(connection.getKey()).thenReturn(new ConnectionKey(FACEBOOK, providerUserId));
    AbstractSocialConnectionRepository socialConnectionRepository =
        mock(AbstractSocialConnectionRepository.class);
    when(
        socialConnectionRepository.findByProviderIdAndProviderUserId(FACEBOOK, providerUserId))
        .thenReturn(new ArrayList());
    ConnectionSignUp signUp = mock(ConnectionSignUp.class);
    when(signUp.execute(connection)).thenReturn(null);
    repository = new StringUsersConnectionRepository(signUp, socialConnectionRepository,
        textEncryptor, connectionFactoryLocator);
    assertEquals(
        Collections.emptyList(),
        repository.findUserIdsWithConnection(connection));
  }

  @Test
  public void testFindConnectionsForIds() {
    Set<String> providerUserIds = new HashSet<>(Arrays.asList("1234", "5678", "9010"));
    AbstractSocialConnectionRepository socialConnectionRepository =
        mock(AbstractSocialConnectionRepository.class);

    when(socialConnectionRepository.findByProviderIdAndProviderUserIdIn(FACEBOOK, providerUserIds))
        .thenReturn(Arrays.asList(
            new StringSocialConnection() {{
              setProviderId(FACEBOOK);
              setProviderUserId("1235");
              setUserId("1");
              setId("X");
            }},
            new StringSocialConnection() {{
              setProviderId(FACEBOOK);
              setProviderUserId("9010");
              setUserId("2");
              setId("Y");
            }}
        ));
    repository = new StringUsersConnectionRepository(null, socialConnectionRepository,
        textEncryptor, connectionFactoryLocator);
    assertEquals(
        new HashSet<>(Arrays.asList("1", "2")),
        repository.findUserIdsConnectedTo(FACEBOOK, providerUserIds));
  }
}
