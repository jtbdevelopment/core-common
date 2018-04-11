package com.jtbdevelopment.core.spring.security.crypto.encrypt;

import java.io.IOException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Date: 12/29/14 Time: 12:00 PM
 */
@Component
public class StrongTextEncryptor implements TextEncryptor {

  private final BytesEncryptor bytesEncryptor;
  private final BASE64Encoder encoder = new BASE64Encoder();
  private final BASE64Decoder decoder = new BASE64Decoder();

  public StrongTextEncryptor(
      BytesEncryptor bytesEncryptor) {
    this.bytesEncryptor = bytesEncryptor;
  }

  @Override
  public String encrypt(final String text) {
    if (StringUtils.isEmpty(text)) {
      return text;
    }
    return encoder.encode(bytesEncryptor.encrypt(text.getBytes()));
  }

  @Override
  public String decrypt(final String encryptedText) {
    if (StringUtils.isEmpty(encryptedText)) {
      return encryptedText;
    }
    try {
      byte[] decoded = decoder.decodeBuffer(encryptedText);
      byte[] decrypted = bytesEncryptor.decrypt(decoded);
      return new String(decrypted);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
