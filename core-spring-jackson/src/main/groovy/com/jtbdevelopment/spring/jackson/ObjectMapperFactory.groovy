package com.jtbdevelopment.spring.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 1/13/15
 * Time: 7:41 PM
 */
@CompileStatic
@Component
class ObjectMapperFactory {
    private static ObjectMapper mapper

    @Autowired
    List<AutoRegistrableJsonSerializer> serializers

    @Autowired
    List<AutoRegistrableJsonDeserializer> deserializers

    synchronized ObjectMapper getObjectMapper() {
        if (!mapper) {
            mapper = new ObjectMapper()
            SimpleModule module = new SimpleModule('com.jtbdevelopment.spring.jackson.automatic')
            deserializers.each {
                AutoRegistrableJsonDeserializer deserializer ->
                    module.addDeserializer(deserializer.registerForClass(), deserializer)
            }
            serializers.each {
                AutoRegistrableJsonSerializer serializer ->
                    module.addSerializer(serializer.registerForClass(), serializer)
            }
            mapper.registerModule(module)
        }
        mapper
    }
}