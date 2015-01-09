package com.jtbdevelopment.core.spring

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@SuppressWarnings("GroovyUnusedDeclaration")
@Configuration
@CompileStatic
class CoreSpringConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        def configurer = new PropertySourcesPlaceholderConfigurer()
        configurer.ignoreResourceNotFound = true;
        return configurer
    }
}
