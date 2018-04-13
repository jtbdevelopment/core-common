package com.jtbdevelopment.core.mongo.spring;

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Date: 1/9/15 Time: 6:38 AM
 */
public class AbstractCoreMongoConfiguration extends AbstractMongoConfiguration {

  private final List<MongoConverter> mongoConverters;
  private final MongoProperties mongoProperties;

  public AbstractCoreMongoConfiguration(final List<MongoConverter> mongoConverters,
      final MongoProperties mongoProperties) {
    this.mongoConverters = mongoConverters;
    this.mongoProperties = mongoProperties;
  }

  @Override
  public CustomConversions customConversions() {
    return new MongoCustomConversions(mongoConverters);
  }

  @Override
  @Nullable
  protected String getMappingBasePackage() {
    return "com.jtbdevelopment";
  }

  @Override
  protected Collection<String> getMappingBasePackages() {
    return Collections.singletonList("com.jtbdevelopment");
  }

  @Override
  protected String getDatabaseName() {
    return mongoProperties.getDbName();
  }

  @Override
  public MongoClient mongoClient() {
    MongoClientOptions options = MongoClientOptions
        .builder()
        .writeConcern(mongoProperties.getDbWriteConcern())
        .build();

    if (!StringUtils.isEmpty(mongoProperties.getDbPassword()) &&
        !StringUtils.isEmpty(mongoProperties.getDbUser())) {
      return new MongoClient(
          Collections.singletonList(
              new ServerAddress(mongoProperties.getDbHost(), mongoProperties.getDbPort())
          ),
          Collections.singletonList(
              MongoCredential.createCredential(
                  mongoProperties.getDbUser(),
                  mongoProperties.getDbName(),
                  mongoProperties.getDbPassword().toCharArray())
          ),
          options);
    } else {
      return new MongoClient(
          Collections.singletonList(
              new ServerAddress(mongoProperties.getDbHost(), mongoProperties.getDbPort())
          ),
          options);
    }

  }
}
