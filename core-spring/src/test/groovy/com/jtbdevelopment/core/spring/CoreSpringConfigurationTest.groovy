package com.jtbdevelopment.core.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.support.PropertiesLoaderSupport

import java.lang.reflect.Field

/**
 * Date: 1/9/15
 * Time: 7:01 PM
 */
class CoreSpringConfigurationTest extends GroovyTestCase {
    CoreSpringConfiguration configuration = new CoreSpringConfiguration()

    void testPropertyPlaceHolder() {
        PropertySourcesPlaceholderConfigurer p = configuration.propertyPlaceholderConfigurer()
        assert p
        Field f = PropertiesLoaderSupport.class.getDeclaredField('ignoreResourceNotFound')
        f.accessible = true
        assert f.get(p)
    }

    void testClassAnnotations() {
        assert CoreSpringConfiguration.class.getAnnotation(Configuration.class)
    }

    void testPropertyPlaceHolderAnnotations() {
        assert CoreSpringConfiguration.class.getMethod('propertyPlaceholderConfigurer').isAnnotationPresent(Bean.class)
    }
}
