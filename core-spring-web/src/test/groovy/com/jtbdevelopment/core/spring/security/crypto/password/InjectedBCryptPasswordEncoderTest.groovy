package com.jtbdevelopment.core.spring.security.crypto.password
/**
 * Date: 12/24/14
 * Time: 4:53 PM
 */
class InjectedBCryptPasswordEncoderTest extends GroovyTestCase {
    InjectedBCryptPasswordEncoder encoder = new InjectedBCryptPasswordEncoder()

    void testEncryption() {
        int nextInt = 0
        while (nextInt < 4) {
            //  8 is fast enough for tests but not for real
            nextInt = new Random().nextInt(10)
        }
        encoder.strength = nextInt
        encoder.setUp()

        def PASSWORD = "PASSWORD"
        def ENCODED = encoder.encode(PASSWORD)
        assert encoder.matches(PASSWORD, ENCODED)
        assert !encoder.matches(PASSWORD + "X", ENCODED)
        assert !encoder.matches(PASSWORD, ENCODED + "X")
    }
}
