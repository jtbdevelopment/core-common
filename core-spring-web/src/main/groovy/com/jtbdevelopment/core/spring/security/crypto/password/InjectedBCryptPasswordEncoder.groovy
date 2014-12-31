package com.jtbdevelopment.core.spring.security.crypto.password

import groovy.transform.CompileStatic
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

import java.security.SecureRandom

/**
 * Date: 12/16/14
 * Time: 12:27 PM
 */
@Component
@CompileStatic
class InjectedBCryptPasswordEncoder implements PasswordEncoder {
    final BCryptPasswordEncoder passwordEncoder;

    InjectedBCryptPasswordEncoder() {
        passwordEncoder = new BCryptPasswordEncoder(12, new SecureRandom())
    }

    @Override
    String encode(final CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword)
    }

    @Override
    boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}
