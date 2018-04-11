package com.jtbdevelopment.core.spring.security.crypto.encrypt;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Date: 12/16/14 Time: 10:49 PM
 */
@Component
public class TextEncryptionProperties {

  private static final Logger logger = LoggerFactory.getLogger(TextEncryptionProperties.class);
  @Value("${textEncryption.password:NOTSET}")
  private String password;
  @Value("${textEncryption.salt:NOTSET}")
  private String salt;
  private boolean warnings = true;

  @PostConstruct
  public void testDefaults() {
    if (password.equals("NOTSET") || salt.equals("NOTSET")) {
      warnings = true;
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("textEncryption.password AND/OR textEncryption.salt is using default values!!!");
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("-----------------------------------------------------------------------------");
      logger.warn("-----------------------------------------------------------------------------");
    } else {
      logger.info("text encryption properties correctly initialized with non-default values.");
      warnings = false;
    }

  }

  String getPassword() {
    return password;
  }

  String getSalt() {
    return salt;
  }

  public boolean getWarnings() {
    return warnings;
  }
}
