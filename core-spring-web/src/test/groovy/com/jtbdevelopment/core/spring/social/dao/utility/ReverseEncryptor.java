package com.jtbdevelopment.core.spring.social.dao.utility;

import org.codehaus.groovy.runtime.StringGroovyMethods;
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

    return StringGroovyMethods.reverse(encryptedText);
  }

  @Override
  public String encrypt(final String text) {
    if (StringUtils.isEmpty(text)) {
      return text;
    }

    return StringGroovyMethods.reverse(text);
  }

}
