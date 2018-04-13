package com.jtbdevelopment.core.mongo.spring.converters;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Date: 11/9/2014 Time: 7:03 PM
 */
@Component
public class InstantToStringConverter implements MongoConverter<Instant, String> {

  private static final ZoneId GMT = ZoneId.of("GMT");

  @Override
  public String convert(final Instant source) {
    return ZonedDateTime.ofInstant(source, GMT).format(DateTimeFormatter.ISO_INSTANT);
  }
}
