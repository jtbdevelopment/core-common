package com.jtbdevelopment.core.spring.social.dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.core.spring.social.dao.utility.FakeConnectionFactory;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookConnectionFactory;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterApi;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterConnectionFactory;
import com.jtbdevelopment.core.spring.social.dao.utility.ReverseEncryptor;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;

/**
 * Date: 1/3/2015 Time: 12:10 PM
 */
public abstract class ConnectionTestCase {

  static final String NEWCO = "newco";
  Map<String, FakeConnectionFactory> providers;
  ConnectionFactoryLocator connectionFactoryLocator;
  TextEncryptor textEncryptor = new ReverseEncryptor();

  protected static String reverse(final String input) {
    if (input != null) {
      return new StringBuilder(input).reverse().toString();
    }
    return null;
  }

  public void setUp() throws Exception {
    Map<String, FakeConnectionFactory> map = new HashMap<>();
    map.put(FakeFacebookApi.FACEBOOK, new FakeFacebookConnectionFactory());
    map.put(FakeTwitterApi.TWITTER, new FakeTwitterConnectionFactory());
    providers = map;
    connectionFactoryLocator = mock(ConnectionFactoryLocator.class);
    when(connectionFactoryLocator.registeredProviderIds()).thenReturn(providers.keySet());
    when(connectionFactoryLocator.getConnectionFactory(FakeFacebookApi.FACEBOOK))
        .thenReturn(providers.get(FakeFacebookApi.FACEBOOK));
    when(connectionFactoryLocator.getConnectionFactory(FakeTwitterApi.TWITTER))
        .thenReturn(providers.get(FakeTwitterApi.TWITTER));
    when(connectionFactoryLocator.getConnectionFactory(FakeFacebookApi.class))
        .thenReturn((ConnectionFactory<FakeFacebookApi>) providers.get(FakeFacebookApi.FACEBOOK));
    when(connectionFactoryLocator.getConnectionFactory(FakeTwitterApi.class))
        .thenReturn((ConnectionFactory<FakeTwitterApi>) providers.get(FakeTwitterApi.TWITTER));
  }

}
