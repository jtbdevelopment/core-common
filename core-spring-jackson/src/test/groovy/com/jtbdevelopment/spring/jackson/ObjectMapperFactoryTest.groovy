package com.jtbdevelopment.spring.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.beans.factory.FactoryBeanNotInitializedException

import javax.annotation.PostConstruct
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 1/13/15
 * Time: 7:51 PM
 */
class ObjectMapperFactoryTest extends GroovyTestCase {
    ObjectMapperFactory objectMapperFactory = new ObjectMapperFactory()

    void testIsSingleton() {
        assert objectMapperFactory.isSingleton()
    }

    void testClass() {
        assert ObjectMapper.class.is(objectMapperFactory.objectType)
    }

    void testObjectNotInitializedYet() {
        shouldFail(FactoryBeanNotInitializedException.class) {
            objectMapperFactory.object
        }
    }

    //  Tough to confirm registration other than to do some serialization and deserialization
    void testCreatesObjectMapperCreationAndReuse() {
        objectMapperFactory = new ObjectMapperFactory()
        def numberDeserializer = new NumberDeserializer()
        def integerSerializer = new IntegerSerializer()
        def bigDecimalSerializer = new BigDecimalSerializer()
        objectMapperFactory.serializers = [integerSerializer, bigDecimalSerializer]
        objectMapperFactory.deserializers = [numberDeserializer]
        objectMapperFactory.initializeMapper()

        ObjectMapper mapper = objectMapperFactory.object
        assert mapper
        assert '{"intValue":"INTEGER","decimalValue":"BIGDECIMAL"}' == mapper.writeValueAsString(new SerializeData())
        DeserializeData out = mapper.readValue('{"intValue":"35"}', DeserializeData.class)
        assert out
        assert 5 == out.intValue

    }

    void testJSR310Registration() {
        objectMapperFactory = new ObjectMapperFactory()
        objectMapperFactory.initializeMapper()
        ObjectMapper mapper = objectMapperFactory.object
        assert mapper

        ZonedDateTimeContainer container = new ZonedDateTimeContainer();
        assert '{"aDate":1352946820.000000304}' == mapper.writeValueAsString(container)
        assert container.aDate == mapper.readValue('{"aDate":1352946820.000000304}', ZonedDateTimeContainer.class).aDate
    }

    //  Primary test is that null values don't explode
    void testInitializeWithNullListsOfHelpers() {
        ObjectMapperFactory objectMapperFactory = new ObjectMapperFactory()
        objectMapperFactory.initializeMapper()
        ObjectMapper mapper = objectMapperFactory.getObject()
        assertNotNull mapper
    }

    void testCustomizationsOfModule() {
        ObjectMapperFactory objectMapperFactory = new ObjectMapperFactory()
        objectMapperFactory.customizations = [
                [
                        customizeModule: {
                            SimpleModule module ->
                                module.addAbstractTypeMapping(SomeInterface.class, SomeInterfaceImpl.class)
                        }
                ] as JacksonModuleCustomization
        ]
        objectMapperFactory.initializeMapper()
        ObjectMapper mapper = objectMapperFactory.getObject()
        SomeClassWithInterface c = new SomeClassWithInterface()
        assert '{"anInterface":{"value":"X"}}' == mapper.writeValueAsString(c)

        c = mapper.readValue('{"anInterface":{"value":"Z"}}', SomeClassWithInterface.class)
        assertNotNull c
        assert c.anInterface.value == 'Z'
    }

    void testPostConstructAnnotation() {
        assert ObjectMapperFactory.class.getMethod('initializeMapper').isAnnotationPresent(PostConstruct.class)
    }

    private static interface SomeInterface {}

    private static class SomeInterfaceImpl implements SomeInterface {
        String value = 'X'
    }

    public static class SomeClassWithInterface {
        SomeInterface anInterface = new SomeInterfaceImpl()
    }

    private class IntegerSerializer extends AutoRegistrableJsonSerializer<Integer> {
        @Override
        Class<Integer> registerForClass() {
            return Integer.class
        }

        @Override
        void serialize(
                final Integer value,
                final JsonGenerator jgen,
                final SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString('INTEGER')
        }
    }

    private class BigDecimalSerializer extends AutoRegistrableJsonSerializer<BigDecimal> {
        @Override
        Class<BigDecimal> registerForClass() {
            return BigDecimal.class
        }

        @Override
        void serialize(
                final BigDecimal value,
                final JsonGenerator jgen,
                final SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString('BIGDECIMAL')
        }
    }

    private class NumberDeserializer extends AutoRegistrableJsonDeserializer<Integer> {
        @Override
        Class<Integer> registerForClass() {
            return Integer.class
        }

        @Override
        Integer deserialize(
                final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return new Integer(5)
        }
    }

    private static class ZonedDateTimeContainer {
        public ZonedDateTime aDate = ZonedDateTime.of(2012, 11, 15, 2, 33, 40, 304, ZoneId.of("GMT"))
    }

    private static class SerializeData {
        Integer intValue = 5
        BigDecimal decimalValue = new BigDecimal(32)
    }

    private static class DeserializeData {
        Integer intValue
    }

}
