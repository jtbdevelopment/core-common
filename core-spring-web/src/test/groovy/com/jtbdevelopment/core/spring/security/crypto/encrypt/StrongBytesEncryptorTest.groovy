package com.jtbdevelopment.core.spring.security.crypto.encrypt
/**
 * Date: 12/29/14
 * Time: 11:52 AM
 */
class StrongBytesEncryptorTest extends GroovyTestCase {
    StrongBytesEncryptor encryptor = new StrongBytesEncryptor()

    public void testEncryptor() {
        TextEncryptionProperties properties = new TextEncryptionProperties(password: "APASSWORD", salt: "ASALT")
        encryptor.textEncryptionProperties = properties
        encryptor.setUp()


        def encrypt = encryptor.encrypt("A TEST".bytes)
        assert new String(encrypt) != 'A TEST'
        assert new String(encryptor.decrypt(encrypt)) == "A TEST"
    }

    public void testAssertsOnTooLongPassword() {
        TextEncryptionProperties properties = new TextEncryptionProperties(password: "Bar12345Bar12345X", salt: "ASALT")
        encryptor.textEncryptionProperties = properties
        try {
            encryptor.setUp()
            fail("should exception")
        } catch (IllegalStateException e) {

        }
    }
}
