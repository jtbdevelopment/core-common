package com.jtbdevelopment.core.spring.security.crypto.password;

import groovy.util.GroovyTestCase;
import java.util.Random;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

/**
 * Date: 12/24/14 Time: 4:53 PM
 */
public class InjectedBCryptPasswordEncoderTest extends GroovyTestCase {

  private InjectedBCryptPasswordEncoder encoder = new InjectedBCryptPasswordEncoder();

  @Test
  public void testValueAnnotation() throws NoSuchFieldException {
    assertEquals("${password.strength:12}",
        InjectedBCryptPasswordEncoder.class.getDeclaredField("strength").getAnnotation(Value.class)
            .value());
  }

  @Test
  public void testEncryption() {
    int nextInt = 0;
    while (nextInt < 4) {
      //  8 is fast enough for tests but not for real
      nextInt = new Random().nextInt(10);
    }

    encoder.setStrength(nextInt);
    encoder.setUp();

    String PASSWORD = "PASSWORD";
    String ENCODED = encoder.encode(PASSWORD);
    assertTrue(encoder.matches(PASSWORD, ENCODED));
    assertFalse(encoder.matches(PASSWORD + "X", ENCODED));
    assertFalse(encoder.matches(PASSWORD, ENCODED + "X"));
  }
}
