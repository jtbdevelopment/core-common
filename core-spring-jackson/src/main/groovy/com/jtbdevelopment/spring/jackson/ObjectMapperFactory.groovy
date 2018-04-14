package com.jtbdevelopment.spring.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.FactoryBeanNotInitializedException
import org.springframework.stereotype.Component

/**
 * Date: 1/13/15
 * Time: 7:41 PM
 */
@CompileStatic
@Component
class ObjectMapperFactory implements FactoryBean<ObjectMapper> {
    private final ObjectMapper objectMapper

    ObjectMapperFactory(
            final List<AutoRegistrableJsonSerializer> serializers,
            final List<AutoRegistrableJsonDeserializer> deserializers,
            final List<JacksonModuleCustomization> customizations
    ) {
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
        objectMapper.registerModule(new JavaTimeModule())
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