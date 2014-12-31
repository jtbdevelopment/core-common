package com.jtbdevelopment.core.spring.security.crypto.encrypt

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.stereotype.Component
import sun.misc.BASE64Decoder
import sun.misc.BASE64Encoder

/**
 * Date: 12/29/14
 * Time: 12:00 PM
 */
@Component
@CompileStatic
class StrongTextEncryptor implements TextEncryptor {
    @Autowired
    BytesEncryptor bytesEncryptor

    private BASE64Encoder encoder = new BASE64Encoder()
    private BASE64Decoder decoder = new BASE64Decoder()

    @Override
    String encrypt(final String text) {
        return encoder.encode(bytesEncryptor.encrypt(text.bytes))
    }

    @Override
    String decrypt(final String encryptedText) {
        def decoded = decoder.decodeBuffer(encryptedText)
        def decrypted = bytesEncryptor.decrypt(decoded)
        return new String(decrypted)
    }
}
