package com.jtbdevelopment.core.mongo.spring.converters;

import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Date: 11/9/2014 Time: 7:08 PM
 */
@Component
public class StringToInstantConverter implements MongoConverter<String, Instant> {

  @Override
  public Instant convert(final String source) {
    return Instant.parse(source);
  }

}
