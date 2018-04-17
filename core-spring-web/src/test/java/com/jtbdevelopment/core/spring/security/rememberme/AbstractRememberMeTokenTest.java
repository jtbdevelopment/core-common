package com.jtbdevelopment.core.spring.security.rememberme;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import org.junit.Test;

/**
 * Date: 1/2/15 Time: 6:36 AM
 */
public class AbstractRememberMeTokenTest {

  @Test
  public void testConstructor() {
    String user = "user";

    String series = "series";

    String tokenValue = "token";

    Date date = new Date();

    String id = "id";
    StringRememberMeToken token = new StringRememberMeToken(user, series, tokenValue, date, id);

    assertEquals(id, token.getId());
    assertEquals(date, token.getDate());
    assertEquals(series, token.getSeries());
    assertEquals(tokenValue, token.getTokenValue());
    assertEquals(user, token.getUsername());
  }

  private static class StringRememberMeToken extends AbstractRememberMeToken<String> {

    private String id;

    StringRememberMeToken(final String username, final String series,
        final String tokenValue, final Date date, final String id) {
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
}
