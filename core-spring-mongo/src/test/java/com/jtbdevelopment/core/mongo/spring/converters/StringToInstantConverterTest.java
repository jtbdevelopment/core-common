package com.jtbdevelopment.core.mongo.spring.converters;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Test;

public class StringToInstantConverterTest {

  @Test
  public void testConvert() {
    StringToInstantConverter converter = new StringToInstantConverter();
    Instant expected = ZonedDateTime.of(
        2014, 11, 10,
        0, 35, 15, 809 * 1000000,
        ZoneId.of("GMT")).toInstant();
    assertEquals(expected, converter.convert("2014-11-10T00:35:15.809Z"));
  }

}
