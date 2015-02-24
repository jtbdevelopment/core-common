package com.jtbdevelopment.core.spring

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@SuppressWarnings("GroovyUnusedDeclaration")
@Configuration
@ComponentScan(basePackages = ['com.jtbdevelopment'])
@CompileStatic
class CoreSpringConfiguration {
    @Bean
    public PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        def configurer = new PropertySourcesPlaceholderConfigurer()
        configurer.ignoreResourceNotFound = true;
        return configurer
    }
}
