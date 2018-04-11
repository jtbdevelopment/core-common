package com.jtbdevelopment.core.spring.security.crypto.encrypt

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 12/29/14
 * Time: 11:52 AM
 */
class StrongBytesEncryptorTest extends GroovyTestCase {
    TextEncryptionProperties properties = mock(TextEncryptionProperties.class)

    void testEncryptor() {
        when(properties.password).thenReturn("APASSWORD")
        when(properties.salt).thenReturn("ASALT")

        StrongBytesEncryptor encryptor = new StrongBytesEncryptor(properties)

        def encrypt = encryptor.encrypt("A TEST".bytes)
        assert new String(encrypt) != 'A TEST'
        assert new String(encryptor.decrypt(encrypt)) == "A TEST"
    }

    void testAssertsOnTooLongPassword() {
        when(properties.password).thenReturn("Bar12345Bar12345X")
        when(properties.salt).thenReturn("ASALT")
        shouldFail(IllegalStateException.class) {
            new StrongBytesEncryptor(properties)
        }
    }
}
