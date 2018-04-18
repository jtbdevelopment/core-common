package com.jtbdevelopment.core.mongo.spring;

import com.mongodb.WriteConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 12/1/14 Time: 10:24 PM
 */
@Component
public class MongoProperties {

  private static final Logger logger = LoggerFactory.getLogger(MongoProperties.class);
  private final String dbName;
  private final String dbHost;
  private final int dbPort;
  private final String dbUser;
  private final String dbPassword;
  private final WriteConcern dbWriteConcern;
  private boolean warnings = false;

  //  TODO - test @Value
  public MongoProperties(
      @Value("${mongo.dbName:}") final String dbName,
      @Value("${mongo.host:localhost}") final String dbHost,
      @Value("${mongo.port:27017}") final int dbPort,
      @Value("${mongo.userName:}") final String dbUser,
      @Value("${mongo.userPassword:}") final String dbPassword,
      @Value("${mongo.writeConcern:JOURNALED}") final String dbWriteConcern
  ) {
    this.dbName = dbName;
    this.dbPassword = dbPassword;
    this.dbWriteConcern = WriteConcern.valueOf(dbWriteConcern);
    this.dbPort = dbPort;
    this.dbUser = dbUser;
    this.dbHost = dbHost;
    logger.info("Connecting to mongo with host:port " + dbHost + ":" + dbPort);
    if (StringUtils.isEmpty(dbName)) {
      logger.warn("No mongo.dbName specified");
      warnings = true;
    } else {
      logger.info("Connecting to " + dbName);
    }
    logger.info("Using writeconcern = " + dbWriteConcern);
    if (StringUtils.isEmpty(dbPassword) && StringUtils.isEmpty(dbUser)) {
      //  OK even if not recommended
      logger.warn("Using unauthenticated connection.");
    } else {
      logger.info("Using authenticated connection.");
      if (StringUtils.isEmpty(dbPassword) || StringUtils.isEmpty(dbUser)) {
        warnings = true;
        logger.warn("-----------------------------------------------------");
        logger.warn("WARNING:  Connecting with a missing user or password.");
        logger.warn("-----------------------------------------------------");
      }

    }
  }

  public String getDbName() {
    return dbName;
  }

  public String getDbHost() {
    return dbHost;
  }

  public int getDbPort() {
    return dbPort;
  }

  public String getDbUser() {
    return dbUser;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public WriteConcern getDbWriteConcern() {
    return dbWriteConcern;
  }

  public boolean isWarnings() {
    return warnings;
  }
}
