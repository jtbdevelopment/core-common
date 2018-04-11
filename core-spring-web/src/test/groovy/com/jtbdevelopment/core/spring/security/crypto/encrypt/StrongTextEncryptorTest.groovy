package com.jtbdevelopment.core.spring.security.crypto.encrypt

import org.springframework.security.crypto.encrypt.BytesEncryptor
import sun.misc.BASE64Encoder

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 12/29/14
 * Time: 12:14 PM
 */
class StrongTextEncryptorTest extends GroovyTestCase {
    BytesEncryptor encryptor = mock(BytesEncryptor.class)
    StrongTextEncryptor textEncryptor = new StrongTextEncryptor(encryptor)

    void testEncryptor() {
        String textToEncrypt = "A TEST"
        byte[] encryptedBytes = [0, 1, 2, 3, 4]
        when(encryptor.decrypt(encryptedBytes)).thenReturn(textToEncrypt.bytes)
        when(encryptor.encrypt(textToEncrypt.bytes)).thenReturn(encryptedBytes)

        String encrypted = textEncryptor.encrypt(textToEncrypt)
        assert encrypted == new BASE64Encoder().encode(encryptedBytes)

        assert textToEncrypt == textEncryptor.decrypt(encrypted)
    }

    void testEncryptNullOrEmpty() {
        assert textEncryptor.encrypt(null) == null
        assert textEncryptor.encrypt("") == ""
    }

    void testDecryptNullOrEmpty() {
        assert textEncryptor.decrypt(null) == null
        assert textEncryptor.decrypt("") == ""
    }
}
