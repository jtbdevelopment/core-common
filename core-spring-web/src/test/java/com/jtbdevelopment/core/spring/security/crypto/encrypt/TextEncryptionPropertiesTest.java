package com.jtbdevelopment.core.spring.security.crypto.encrypt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Date: 1/2/15 Time: 6:11 PM
 */
public class TextEncryptionPropertiesTest {

  @Test
  public void testTestDefaultsGenerateWarning() {
    TextEncryptionProperties properties = new TextEncryptionProperties("", "");
    assertTrue(properties.isWarnings());
  }

  @Test
  public void testTestPasswordDefaultsGenerateWarning() {
    TextEncryptionProperties properties = new TextEncryptionProperties(null, "X");
    assertTrue(properties.isWarnings());
  }

  @Test
  public void testTestSaltDefaultsGenerateWarning() {
    TextEncryptionProperties properties = new TextEncryptionProperties("X", null);
    assertTrue(properties.isWarnings());
  }

  @Test
  public void testTestProperlySetNoWarnings() {
    TextEncryptionProperties properties = new TextEncryptionProperties("X", "Y");
    assertFalse(properties.isWarnings());
  }

}
