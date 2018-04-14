package com.jtbdevelopment.core.spring.security.crypto.password;

import java.security.SecureRandom;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Date: 12/16/14 Time: 12:27 PM
 */
@Component
public class InjectedBCryptPasswordEncoder implements PasswordEncoder {

  @Value("${password.strength:12}")
  private int strength;

  private BCryptPasswordEncoder passwordEncoder;

  @PostConstruct
  public BCryptPasswordEncoder setUp() {
    return passwordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());
  }

  @Override
  public String encode(final CharSequence rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  public void setStrength(int strength) {
    this.strength = strength;
  }

}
