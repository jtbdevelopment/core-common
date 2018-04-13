package com.jtbdevelopment.core.spring.security.crypto.encrypt
/**
 * Date: 1/2/15
 * Time: 6:11 PM
 */
class TextEncryptionPropertiesTest extends GroovyTestCase {

    void testTestDefaultsGenerateWarning() {
        TextEncryptionProperties properties = new TextEncryptionProperties("", "")
        assert properties.warnings
    }

    void testTestPasswordDefaultsGenerateWarning() {
        TextEncryptionProperties properties = new TextEncryptionProperties(null, "X")
        assert properties.warnings
    }

    void testTestSaltDefaultsGenerateWarning() {
        TextEncryptionProperties properties = new TextEncryptionProperties("X", null)
        assert properties.warnings
    }

    void testTestProperlySetNoWarnings() {
        TextEncryptionProperties properties = new TextEncryptionProperties("X", "Y")
        assertFalse properties.warnings
    }
}
