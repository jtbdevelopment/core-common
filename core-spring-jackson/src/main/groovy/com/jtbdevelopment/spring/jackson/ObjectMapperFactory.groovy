package com.jtbdevelopment.spring.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.FactoryBeanNotInitializedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 1/13/15
 * Time: 7:41 PM
 */
@CompileStatic
@Component
class ObjectMapperFactory implements FactoryBean<ObjectMapper> {
    private ObjectMapper objectMapper

    @Autowired(required = false)
    List<AutoRegistrableJsonSerializer> serializers

    @Autowired(required = false)
    List<AutoRegistrableJsonDeserializer> deserializers

    @Autowired(required = false)
    List<JacksonModuleCustomization> customizations

    @PostConstruct
    void initializeMapper() {
        objectMapper = new ObjectMapper()
        SimpleModule module = new SimpleModule('com.jtbdevelopment.spring.jackson.automatic')
        deserializers && deserializers.each {
            AutoRegistrableJsonDeserializer deserializer ->
                module.addDeserializer(deserializer.registerForClass(), deserializer)
        }
        serializers && serializers.each {
            AutoRegistrableJsonSerializer serializer ->
                module.addSerializer(serializer.registerForClass(), serializer)
        }
        customizations && customizations.each {
            it.customizeModule(module)
        }
        objectMapper.registerModule(module)
    }

    @Override
    ObjectMapper getObject() throws Exception {
        if (objectMapper) {
            return objectMapper
        }
        throw new FactoryBeanNotInitializedException()
    }

    @Override
    Class<?> getObjectType() {
        return ObjectMapper.class
    }

    @Override
    boolean isSingleton() {
        return true
    }
}