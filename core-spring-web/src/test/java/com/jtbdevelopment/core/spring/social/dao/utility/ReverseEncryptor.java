package com.jtbdevelopment.core.spring.social.dao.utility;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.StringUtils;

/**
 * Date: 1/4/2015 Time: 7:48 AM
 */
public class ReverseEncryptor implements TextEncryptor {

  @Override
  public String decrypt(final String encryptedText) {
    if (StringUtils.isEmpty(encryptedText)) {
      return encryptedText;
    }

    return new StringBuilder(encryptedText).reverse().toString();
  }

  @Override
  public String encrypt(final String text) {
    if (StringUtils.isEmpty(text)) {
      return text;
    }

    return new StringBuilder(text).reverse().toString();
  }

}
