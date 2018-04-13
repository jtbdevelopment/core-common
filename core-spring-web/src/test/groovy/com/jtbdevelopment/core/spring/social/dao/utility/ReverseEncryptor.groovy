package com.jtbdevelopment.core.spring.social.dao.utility

import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.util.StringUtils

/**
 * Date: 1/4/2015
 * Time: 7:48 AM
 */
class ReverseEncryptor implements TextEncryptor {
    @Override
    String decrypt(final String encryptedText) {
        if (StringUtils.isEmpty(encryptedText)) {
            return encryptedText
        }
        return encryptedText.reverse()
    }

    @Override
    String encrypt(final String text) {
        if (StringUtils.isEmpty(text)) {
            return text
        }
        return text.reverse()
    }
}
