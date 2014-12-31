package com.jtbdevelopment.core.spring.security.crypto.password
/**
 * Date: 12/24/14
 * Time: 4:53 PM
 */
class InjectedBCryptPasswordEncoderTest extends GroovyTestCase {
    InjectedBCryptPasswordEncoder encoder = new InjectedBCryptPasswordEncoder()

    void testEncryption() {
        def PASSWORD = "PASSWORD"
        def ENCODED = encoder.encode(PASSWORD)
        assert encoder.matches(PASSWORD, ENCODED)
        assert !encoder.matches(PASSWORD + "X", ENCODED)
        assert !encoder.matches(PASSWORD, ENCODED + "X")
    }
}
