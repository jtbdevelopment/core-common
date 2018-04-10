package com.jtbdevelopment.core.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SuppressWarnings("GroovyUnusedDeclaration")
@Configuration
@ComponentScan(basePackages = {"com.jtbdevelopment"})
public class CoreSpringConfiguration {
    @Bean
    public PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        return configurer;
    }

}
