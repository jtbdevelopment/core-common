package com.jtbdevelopment.core.spring.social.dao.utility

import org.springframework.security.crypto.encrypt.TextEncryptor

/**
 * Date: 1/4/2015
 * Time: 7:48 AM
 */
class ReverseEncryptor implements TextEncryptor {
    @Override
    String decrypt(final String encryptedText) {
        return encryptedText.reverse()
    }

    @Override
    String encrypt(final String text) {
        return text.reverse()
    }
}
