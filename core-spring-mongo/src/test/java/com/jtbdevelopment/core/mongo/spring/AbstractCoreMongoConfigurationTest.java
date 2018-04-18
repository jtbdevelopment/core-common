package com.jtbdevelopment.core.mongo.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * Date: 1/9/15 Time: 6:51 PM
 */
public class AbstractCoreMongoConfigurationTest {

  @Test
  public void testCustomConversions() {
    SToCC cc1 = new SToCC();
    CCToI cc2 = new CCToI();

    MongoConfiguration configuration = new MongoConfiguration(Arrays.asList(cc1, cc2), null);
    GenericConversionService service = new GenericConversionService();
    configuration.customConversions().registerConvertersIn(service);
    assertTrue(service.canConvert(String.class, ConvertibleClass.class));
    assertTrue(service.canConvert(ConvertibleClass.class, Integer.class));
  }

  @Test
  public void testGetMappingBasePackage() {
    MongoConfiguration configuration = new MongoConfiguration(null, null);
    assertEquals("com.jtbdevelopment", configuration.getMappingBasePackage());
    assertEquals(
        Collections.singletonList("com.jtbdevelopment"),
        configuration.getMappingBasePackages());
  }

  @Test
  public void testGetDatabaseName() {
    MongoProperties p = new MongoProperties("X", null, 0, null, null, "JOURNALED");
    MongoConfiguration configuration = new MongoConfiguration(new ArrayList<>(), p);

    assertEquals(configuration.getDatabaseName(), p.getDbName());
  }

  private static class ConvertibleClass {

  }

  private static class SToCC implements MongoConverter<String, ConvertibleClass> {

    @Override
    public ConvertibleClass convert(String source) {
      return null;
    }

  }

  private static class CCToI implements MongoConverter<ConvertibleClass, Integer> {

    @Override
    public Integer convert(ConvertibleClass source) {
      return null;
    }

  }
}
