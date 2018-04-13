package com.jtbdevelopment.core.mongo.spring;

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Date: 1/9/15 Time: 6:38 AM
 */
@EnableMongoRepositories("com.jtbdevelopment")
@EnableMongoAuditing
@Configuration
public class MongoConfiguration extends AbstractCoreMongoConfiguration {

  public MongoConfiguration(
      final List<MongoConverter> mongoConverters,
      final MongoProperties mongoProperties) {
    super(mongoConverters, mongoProperties);
  }
}
