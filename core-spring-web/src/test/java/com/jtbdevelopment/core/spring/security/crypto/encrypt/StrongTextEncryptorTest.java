package com.jtbdevelopment.core.spring.security.crypto.encrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import sun.misc.BASE64Encoder;

/**
 * Date: 12/29/14 Time: 12:14 PM
 */
public class StrongTextEncryptorTest {

  private BytesEncryptor encryptor = mock(BytesEncryptor.class);
  private StrongTextEncryptor textEncryptor = new StrongTextEncryptor(encryptor);

  @Test
  public void testEncryptor() {
    String textToEncrypt = "A TEST";
    byte[] encryptedBytes = new byte[]{0, 1, 2, 3, 4};
    when(encryptor.decrypt(encryptedBytes)).thenReturn(textToEncrypt.getBytes());
    when(encryptor.encrypt(textToEncrypt.getBytes())).thenReturn(encryptedBytes);

    String encrypted = textEncryptor.encrypt(textToEncrypt);
    assertEquals(new BASE64Encoder().encode(encryptedBytes), encrypted);
    assertEquals(textToEncrypt, textEncryptor.decrypt(encrypted));
  }

  @Test
  public void testEncryptNullOrEmpty() {
    assertNull(textEncryptor.encrypt(null));
    assertEquals("", textEncryptor.encrypt(""));
  }

  @Test
  public void testDecryptNullOrEmpty() {
    assertNull(textEncryptor.decrypt(null));
    assertEquals("", textEncryptor.decrypt(""));
  }
}
