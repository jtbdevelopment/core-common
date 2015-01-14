package com.jtbdevelopment.spring.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * Date: 1/13/15
 * Time: 7:51 PM
 */
class ObjectMapperFactoryTest extends GroovyTestCase {
    ObjectMapperFactory objectMapperFactory = new ObjectMapperFactory()

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

    private static class SerializeData {
        Integer intValue = 5
        BigDecimal decimalValue = new BigDecimal(32)
    }

    private static class DeserializeData {
        Integer intValue
    }

    void testCreatesObjectMapperCreationAndReuse() {
        def numberDeserializer = new NumberDeserializer()
        def integerSerializer = new IntegerSerializer()
        def bigDecimalSerializer = new BigDecimalSerializer()
        objectMapperFactory.serializers = [integerSerializer, bigDecimalSerializer]
        objectMapperFactory.deserializers = [numberDeserializer]

        ObjectMapper mapper = objectMapperFactory.getObjectMapper()
        assert mapper
        assert mapper.writeValueAsString(new SerializeData()) == '{"intValue":"INTEGER","decimalValue":"BIGDECIMAL"}'
        DeserializeData out = mapper.readValue('{"intValue":"35"}', DeserializeData.class)
        assert out
        assert out.intValue == 5
    }
}
