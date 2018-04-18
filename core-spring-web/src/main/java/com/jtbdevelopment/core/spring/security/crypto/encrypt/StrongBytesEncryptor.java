package com.jtbdevelopment.core.spring.security.crypto.encrypt;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

/**
 * Date: 12/29/14 Time: 11:37 AM
 *
 * Implements Strong Encryption using base JRE http://www.software-architect.net/articles/using-strong-encryption-in-java/introduction.html
 */
@Component
public class StrongBytesEncryptor implements BytesEncryptor {

  private static final int PASSWORD_LENGTH = 16;
  private final Cipher encrypt;
  private final Cipher decrypt;
  private final byte[] saltBytes;

  public StrongBytesEncryptor(final TextEncryptionProperties textEncryptionProperties) {
    if (textEncryptionProperties.getPassword().length() > PASSWORD_LENGTH) {
      throw new IllegalStateException("textEncryption password is too long");
    }

    saltBytes = textEncryptionProperties.getSalt().getBytes();
    try {
      Key aesKey = new SecretKeySpec(
          padPassword(textEncryptionProperties.getPassword(), PASSWORD_LENGTH).getBytes(),
          "AES");
      encrypt = Cipher.getInstance("AES");
      encrypt.init(Cipher.ENCRYPT_MODE, aesKey);
      decrypt = Cipher.getInstance("AES");
      decrypt.init(Cipher.DECRYPT_MODE, aesKey);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String padPassword(final String password, final int minLength) {
    String newPassword = password;
    while (newPassword.length() < minLength) {
      newPassword = " " + newPassword;
    }
    return newPassword;
  }

  @Override
  public byte[] encrypt(final byte[] byteArray) {
    try {
      byte[] salted = new byte[byteArray.length + (saltBytes.length * 2)];
      System.arraycopy(saltBytes, 0, salted, 0, saltBytes.length);
      System.arraycopy(byteArray, 0, salted, saltBytes.length, byteArray.length);
      System
          .arraycopy(saltBytes, 0, salted, (saltBytes.length + byteArray.length), saltBytes.length);
      return encrypt.doFinal(salted);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] decrypt(final byte[] encryptedByteArray) {
    try {
      byte[] salted = decrypt.doFinal(encryptedByteArray);
      byte[] result = new byte[salted.length - (saltBytes.length * 2)];
      System.arraycopy(salted, saltBytes.length, result, 0, result.length);
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
