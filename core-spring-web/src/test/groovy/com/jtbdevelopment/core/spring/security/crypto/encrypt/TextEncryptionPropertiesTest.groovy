package com.jtbdevelopment.core.spring.security.crypto.encrypt

import org.springframework.beans.factory.annotation.Value

import javax.annotation.PostConstruct
import java.lang.reflect.Method

/**
 * Date: 1/2/15
 * Time: 6:11 PM
 */
class TextEncryptionPropertiesTest extends GroovyTestCase {
    TextEncryptionProperties properties = new TextEncryptionProperties()

    void testPostConstructAnnotation() {
        Method m = TextEncryptionProperties.class.getMethod('testDefaults')
        assert m.getAnnotation(PostConstruct.class)
    }

    void testValueAnnotations() {
        assert TextEncryptionProperties.class.
                getDeclaredField('password')?.
                getAnnotation(Value.class)?.
                value() == '${textEncryption.password:NOTSET}'
        assert TextEncryptionProperties.class.
                getDeclaredField('salt')?.
                getAnnotation(Value.class)?.
                value() == '${textEncryption.salt:NOTSET}'
    }

    void testTestDefaultsGenerateWarning() {
        properties.password = 'NOTSET'
        properties.salt = 'NOTSET'
        assert properties.warnings
        properties.testDefaults()
        assert properties.warnings
    }

    void testTestPasswordDefaultsGenerateWarning() {
        properties.password = 'NOTSET'
        properties.salt = 'SET'
        assert properties.warnings
        properties.testDefaults()
        assert properties.warnings
    }

    void testTestSaltDefaultsGenerateWarning() {
        properties.password = 'SET'
        properties.salt = 'NOTSET'
        assert properties.warnings
        properties.testDefaults()
        assert properties.warnings
    }

    void testTestProperlySetNoWarnings() {
        properties.password = 'SET'
        properties.salt = 'SET'
        assert properties.warnings
        properties.testDefaults()
        assertFalse properties.warnings
    }
}
