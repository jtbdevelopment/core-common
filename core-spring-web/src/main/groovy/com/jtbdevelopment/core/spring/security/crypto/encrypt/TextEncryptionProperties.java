package com.jtbdevelopment.core.spring.security.crypto.encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 12/16/14 Time: 10:49 PM
 */
@Component
public class TextEncryptionProperties {

  private static final Logger logger = LoggerFactory.getLogger(TextEncryptionProperties.class);
  private final String password;
  private final String salt;
  private final boolean warnings;

  //  TODO - test @Value
  public TextEncryptionProperties(
      @Value("${textEncryption.password:}") final String password,
      @Value("${textEncryption.salt:}") final String salt
  ) {
    this.password = password;
    this.salt = salt;
    this.warnings = StringUtils.isEmpty(password) || StringUtils.isEmpty(salt);
    if (warnings) {
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("textEncryption.password AND/OR textEncryption.salt is using default values!!!");
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("-----------------------------------------------------------------------------");
    } else {
      logger.info("text encryption properties correctly initialized with non-default values.");
    }
  }

  public boolean isWarnings() {
    return warnings;
  }

  String getPassword() {
    return password;
  }

  String getSalt() {
    return salt;
  }

}
