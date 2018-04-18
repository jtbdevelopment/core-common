package com.jtbdevelopment.core.mongo.spring.converters;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Test;

public class InstantToStringConverterTest {

  @Test
  public void testConvert() {
    ZonedDateTime zonedTime = ZonedDateTime.of(
        2014, 1, 1,
        14, 32, 19, 800 * 1000000,
        ZoneId.of("Europe/Paris"));
    assertEquals(
        "2014-01-01T13:32:19.800Z",
        new InstantToStringConverter().convert(zonedTime.toInstant())
    );
  }

}
