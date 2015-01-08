package com.jtbdevelopment.core.spring.security.crypto.encrypt

import org.springframework.context.annotation.Bean

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

    void testTestDefaultsGenerateWarning() {
        properties.password = 'NOTSET'
        properties.salt = 'NOTSET'
        assertFalse properties.warnings
        properties.testDefaults()
        assert properties.warnings
    }

    void testTestPasswordDefaultsGenerateWarning() {
        properties.password = 'NOTSET'
        properties.salt = 'SET'
        assertFalse properties.warnings
        properties.testDefaults()
        assert properties.warnings
    }

    void testTestSaltDefaultsGenerateWarning() {
        properties.password = 'SET'
        properties.salt = 'NOTSET'
        assertFalse properties.warnings
        properties.testDefaults()
        assert properties.warnings
    }

    void testTestProperlySetNoWarnings() {
        properties.password = 'SET'
        properties.salt = 'SET'
        assertFalse properties.warnings
        properties.testDefaults()
        assertFalse properties.warnings
    }
}
