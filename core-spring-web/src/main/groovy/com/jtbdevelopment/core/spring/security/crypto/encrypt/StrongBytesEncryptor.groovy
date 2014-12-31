package com.jtbdevelopment.core.spring.security.crypto.encrypt

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.Key

/**
 * Date: 12/29/14
 * Time: 11:37 AM
 *
 * Implements Strong Encryption using base JRE
 * http://www.software-architect.net/articles/using-strong-encryption-in-java/introduction.html
 *
 */
@Component
@CompileStatic
class StrongBytesEncryptor implements BytesEncryptor {
    @Autowired
    TextEncryptionProperties textEncryptionProperties

    private Cipher encrypt;
    private Cipher decrypt;
    private byte[] saltBytes;

    @PostConstruct
    public void setUp() {
        if (textEncryptionProperties.password.length() > 16) {
            throw new IllegalStateException("textEncryption password is too long")
        }

        saltBytes = textEncryptionProperties.salt.bytes
        Key aesKey = new SecretKeySpec(textEncryptionProperties.password.padLeft(16, " ").getBytes(), "AES");
        encrypt = Cipher.getInstance("AES");
        encrypt.init(Cipher.ENCRYPT_MODE, aesKey);
        decrypt = Cipher.getInstance("AES");
        decrypt.init(Cipher.DECRYPT_MODE, aesKey);
    }

    @Override
    byte[] encrypt(final byte[] byteArray) {
        byte[] salted = new byte[byteArray.length + (saltBytes.length * 2)]
        System.arraycopy(saltBytes, 0, salted, 0, saltBytes.length)
        System.arraycopy(byteArray, 0, salted, saltBytes.length, byteArray.length)
        System.arraycopy(saltBytes, 0, salted, (saltBytes.length + byteArray.length), saltBytes.length)
        return encrypt.doFinal(salted)
    }

    @Override
    byte[] decrypt(final byte[] encryptedByteArray) {
        byte[] salted = decrypt.doFinal(encryptedByteArray)
        byte[] result = new byte[salted.length - (saltBytes.length * 2)]
        System.arraycopy(salted, saltBytes.length, result, 0, result.length)
        return result
    }
}
