package com.jtbdevelopment.core.spring.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/13/15 Time: 7:51 PM
 */
public class ObjectMapperFactoryTest {

  private ObjectMapperFactory objectMapperFactory = new ObjectMapperFactory(
      new ArrayList<>(),
      new ArrayList<>(), new ArrayList<>());

  @Test
  public void testIsSingleton() {
    assertTrue(objectMapperFactory.isSingleton());
  }

  @Test
  public void testClass() {
    assertEquals(objectMapperFactory.getObjectType(), ObjectMapper.class);
  }

  @Test
  public void testCreatesObjectMapperCreationAndReuse() throws Exception {
    NumberDeserializer numberDeserializer = new NumberDeserializer();
    IntegerSerializer integerSerializer = new IntegerSerializer();
    BigDecimalSerializer bigDecimalSerializer = new BigDecimalSerializer();
    ObjectMapperFactory objectMapperFactory = new ObjectMapperFactory(
        Arrays.asList(integerSerializer, bigDecimalSerializer),
        Collections.singletonList(numberDeserializer),
        new ArrayList<>());

    ObjectMapper mapper = objectMapperFactory.getObject();
    assertEquals("{\"intValue\":\"INTEGER\",\"decimalValue\":\"BIGDECIMAL\"}",
        mapper.writeValueAsString(new SerializeData()));
    DeserializeData out = mapper.readValue("{\"intValue\":\"35\"}", DeserializeData.class);
    assertEquals((Integer) 5, out.getIntValue());

  }

  @Test
  public void testJSR310Registration() throws Exception {
    objectMapperFactory = new ObjectMapperFactory(
        new ArrayList<>(), new ArrayList<>(),
        new ArrayList<>());
    ObjectMapper mapper = objectMapperFactory.getObject();

    ZonedDateTimeContainer container = new ZonedDateTimeContainer();
    assertEquals("{\"aDate\":1352946820.000000304}", mapper.writeValueAsString(container));
    assertEquals(container.aDate,
        mapper.readValue("{\"aDate\":1352946820.000000304}", ZonedDateTimeContainer.class).aDate);
  }

  @Test
  public void testCustomizationsOfModule() throws Exception {
    List<JacksonModuleCustomization> customizations = new ArrayList<>(
        Arrays.asList((JacksonModuleCustomization) module -> module
            .addAbstractTypeMapping(SomeInterface.class, SomeInterfaceImpl.class))
    );
    objectMapperFactory = new ObjectMapperFactory(new ArrayList<>(), new ArrayList<>(),
        customizations);
    ObjectMapper mapper = objectMapperFactory.getObject();
    SomeClassWithInterface c = new SomeClassWithInterface();
    assertEquals("{\"anInterface\":{\"value\":\"X\"}}", mapper.writeValueAsString(c));

    c = mapper.readValue("{\"anInterface\":{\"value\":\"Z\"}}", SomeClassWithInterface.class);
    Assert.assertNotNull(c);
    assertEquals("Z", c.getAnInterface().getValue());
  }

  private interface SomeInterface {

  }

  private static class SomeInterfaceImpl implements SomeInterface {

    private String value = "X";

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  public static class SomeClassWithInterface {

    private SomeInterfaceImpl anInterface = new SomeInterfaceImpl();

    public SomeInterfaceImpl getAnInterface() {
      return anInterface;
    }

    public void setAnInterface(SomeInterfaceImpl anInterface) {
      this.anInterface = anInterface;
    }
  }

  private static class ZonedDateTimeContainer {

    public ZonedDateTime aDate = ZonedDateTime.of(2012, 11, 15, 2, 33, 40, 304, ZoneId.of("UTC"));
  }

  private static class SerializeData {

    private Integer intValue = 5;
    private BigDecimal decimalValue = new BigDecimal(32);

    public Integer getIntValue() {
      return intValue;
    }

    public void setIntValue(Integer intValue) {
      this.intValue = intValue;
    }

    public BigDecimal getDecimalValue() {
      return decimalValue;
    }

    public void setDecimalValue(BigDecimal decimalValue) {
      this.decimalValue = decimalValue;
    }
  }

  private static class DeserializeData {

    private Integer intValue;

    Integer getIntValue() {
      return intValue;
    }

    public void setIntValue(Integer intValue) {
      this.intValue = intValue;
    }
  }

  private class IntegerSerializer extends AutoRegistrableJsonSerializer<Integer> {

    @Override
    public Class<Integer> handledType() {
      return Integer.class;
    }

    @Override
    public void serialize(final Integer value, final JsonGenerator jgen,
        final SerializerProvider provider) throws IOException {
      jgen.writeString("INTEGER");
    }

  }

  private class BigDecimalSerializer extends AutoRegistrableJsonSerializer<BigDecimal> {

    @Override
    public Class<BigDecimal> handledType() {
      return BigDecimal.class;
    }

    @Override
    public void serialize(final BigDecimal value, final JsonGenerator jgen,
        final SerializerProvider provider) throws IOException {
      jgen.writeString("BIGDECIMAL");
    }

  }

  private class NumberDeserializer extends AutoRegistrableJsonDeserializer<Integer> {

    @Override
    public Class<Integer> handledType() {
      return Integer.class;
    }

    @Override
    public Integer deserialize(final JsonParser jp, final DeserializationContext ctxt)
        throws IOException {
      return 5;
    }

  }
}
