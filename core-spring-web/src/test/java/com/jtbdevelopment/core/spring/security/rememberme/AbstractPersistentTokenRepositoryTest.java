package com.jtbdevelopment.core.spring.security.rememberme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * Date: 1/2/15 Time: 6:42 AM
 *
 * Loosely based on spring's own JdbcTokenRepositoryImplTests
 */
public class AbstractPersistentTokenRepositoryTest {

  protected AbstractRememberMeTokenRepository tokenRepository = mock(
      AbstractRememberMeTokenRepository.class);
  protected AbstractPersistentTokenRepository repository;

  @Before
  public void setup() {
    repository = new TestPersistentTokenRepository(tokenRepository);
  }

  @Test
  public void testCreateNewTokenInsertsCorrectData() {
    Date currentDate = new Date();
    PersistentRememberMeToken token = new PersistentRememberMeToken("joeuser", "joesseries",
        "atoken", currentDate);

    repository.createNewToken(token);

    ArgumentCaptor<AbstractRememberMeToken> captor = ArgumentCaptor
        .forClass(AbstractRememberMeToken.class);
    verify(tokenRepository).save(captor.capture());
    AbstractRememberMeToken saved = captor.getValue();
    assertNotSame(token, saved);
    assertNull(saved.getId());
    assertEquals("joeuser", saved.getUsername());
    assertEquals("joesseries", saved.getSeries());
    assertEquals(currentDate, saved.getDate());
    assertEquals("atoken", saved.getTokenValue());
  }

  @Test
  public void testRetrievingTokenReturnsCorrectData() {
    String series = "joesseries";
    AbstractRememberMeToken token = repository
        .newToken(new PersistentRememberMeToken("joeuser", series, "atoken", new Date()));
    when(tokenRepository.findBySeries(series)).thenReturn(token);

    PersistentRememberMeToken loaded = repository.getTokenForSeries(series);

    assertSame(token, loaded);
  }

  @Test
  public void testRemovingUserTokensDeletesData() {
    AbstractRememberMeToken token1 = repository
        .newToken(new PersistentRememberMeToken("joeuser", "series1", "atoken1", new Date()));
    AbstractRememberMeToken token2 = repository
        .newToken(new PersistentRememberMeToken("joeuser", "series2", "atoken2", new Date()));
    when(tokenRepository.findByUsername("joeuser"))
        .thenReturn(new ArrayList<>(Arrays.asList(token1, token2)));
    repository.removeUserTokens("joeuser");
    verify(tokenRepository).delete(token1);
    verify(tokenRepository).delete(token2);
  }

  @Test
  public void testUpdatingTokenModifiesTokenValueAndLastUsed() {
    AbstractRememberMeToken initialToken = repository
        .newToken(new PersistentRememberMeToken("joeuser", "joesseries", "atoken", new Date()));

    Date newDate = new Date();
    String newToken = "newtoken";
    when(tokenRepository.findBySeries("joesseries")).thenReturn(initialToken);
    when(tokenRepository.save(initialToken)).thenReturn(initialToken);
    repository.updateToken("joesseries", newToken, newDate);
    ArgumentCaptor<AbstractRememberMeToken> captor = ArgumentCaptor
        .forClass(AbstractRememberMeToken.class);
    verify(tokenRepository).save(captor.capture());
    AbstractRememberMeToken token = captor.getValue();
    assertEquals(initialToken.getId(), token.getId());
    assertEquals("joeuser", token.getUsername());
    assertEquals("joesseries", token.getSeries());
    assertEquals(newDate, token.getDate());
    assertEquals(newToken, token.getTokenValue());
  }

  @Test
  public void testUpdatingTokenWithNoSeries() {
    Date newDate = new Date();
    String newToken = "newtoken";
    when(tokenRepository.findBySeries("joesseries")).thenReturn(null);
    repository.updateToken("joesseries", newToken, newDate);
    verify(tokenRepository, never()).save(any());
  }

  private static class TestPersistentToken extends AbstractRememberMeToken<String> {

    private String id;

    TestPersistentToken(final String username, final String series, final String tokenValue,
        final Date date, final String id) {
      super(username, series, tokenValue, date);
      this.id = id;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  private static class TestPersistentTokenRepository extends
      AbstractPersistentTokenRepository<String, TestPersistentToken> {

    TestPersistentTokenRepository(
        AbstractRememberMeTokenRepository<String, TestPersistentToken> rememberMeTokenRepository) {
      super(rememberMeTokenRepository);
    }

    @Override
    public TestPersistentToken newToken(final PersistentRememberMeToken source) {
      return newToken(null, source.getUsername(), source.getSeries(), source.getTokenValue(),
          source.getDate());
    }

    @Override
    public TestPersistentToken newToken(final String s, final String username, final String series,
        final String tokenValue, final Date date) {
      return new TestPersistentToken(username, series, tokenValue, date, s);
    }

  }
}
