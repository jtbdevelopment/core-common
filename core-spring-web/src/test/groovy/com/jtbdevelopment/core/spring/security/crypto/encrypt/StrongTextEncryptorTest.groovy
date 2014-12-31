package com.jtbdevelopment.core.spring.security.crypto.encrypt

import org.springframework.security.crypto.encrypt.BytesEncryptor
import sun.misc.BASE64Encoder

/**
 * Date: 12/29/14
 * Time: 12:14 PM
 */
class StrongTextEncryptorTest extends GroovyTestCase {
    StrongTextEncryptor textEncryptor = new StrongTextEncryptor()

    public void testEncryptor() {
        String textToEncrypt = "A TEST"
        byte[] encryptedBytes = [0, 1, 2, 3, 4]
        textEncryptor.bytesEncryptor = [
                encrypt: {
                    byte[] it ->
                        assert it == textToEncrypt.bytes
                        return encryptedBytes
                },
                decrypt: {
                    byte[] it ->
                        assert it == encryptedBytes
                        return textToEncrypt.bytes
                }
        ] as BytesEncryptor

        def String encrypted = textEncryptor.encrypt(textToEncrypt)
        assert encrypted == new BASE64Encoder().encode(encryptedBytes)

        assert textToEncrypt == textEncryptor.decrypt(encrypted)
    }
}
