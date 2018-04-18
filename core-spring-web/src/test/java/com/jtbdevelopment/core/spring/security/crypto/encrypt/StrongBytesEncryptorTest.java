package com.jtbdevelopment.core.spring.security.crypto.encrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

/**
 * Date: 12/29/14 Time: 11:52 AM
 */
public class StrongBytesEncryptorTest {

  private TextEncryptionProperties properties = mock(TextEncryptionProperties.class);

  @Test
  public void testEncryptor() {
    when(properties.getPassword()).thenReturn("APASSWORD");
    when(properties.getSalt()).thenReturn("ASALT");

    StrongBytesEncryptor encryptor = new StrongBytesEncryptor(properties);

    byte[] encrypt = encryptor.encrypt("A TEST".getBytes());
    assertNotEquals("A TEST", new String(encrypt));
    assertEquals("A TEST", new String(encryptor.decrypt(encrypt)));
  }

  @Test(expected = IllegalStateException.class)
  public void testAssertsOnTooLongPassword() {
    when(properties.getPassword()).thenReturn("Bar12345Bar12345X");
    when(properties.getSalt()).thenReturn("ASALT");
    new StrongBytesEncryptor(properties);
  }
}
