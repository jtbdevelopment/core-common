package com.jtbdevelopment.core.mongo.spring;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version.Main;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * Date: 1/4/2015 Time: 7:14 PM
 */
public abstract class AbstractMongoNoSpringContextIntegration {

  @SuppressWarnings("WeakerAccess")
  protected static final String DB_NAME = "test";
  @SuppressWarnings("WeakerAccess")
  protected static final int DB_PORT = 13051;
  @SuppressWarnings("WeakerAccess")
  protected static MongoClient mongoClient;
  protected static MongoDatabase db;
  private static MongodExecutable mongodExecutable = null;

  @SuppressWarnings("WeakerAccess")
  protected static void setupMongo() throws Exception {
    if (mongodExecutable != null) {
      return;
    }

    MongodStarter starter = MongodStarter.getDefaultInstance();

    IMongodConfig mongodConfig = new MongodConfigBuilder().version(Main.PRODUCTION)
        .net(new Net(DB_PORT, Network.localhostIsIPv6())).build();

    mongodExecutable = starter.prepare(mongodConfig);
    mongodExecutable.start();

    System.setProperty("mongo.port", String.format("%d", DB_PORT));
    System.setProperty("mongo.dbName", DB_NAME);
    System.setProperty("mongo.writeConcern", "ACKNOWLEDGED");
    mongoClient = new MongoClient("localhost", DB_PORT);
    db = mongoClient.getDatabase(DB_NAME);
  }

  @SuppressWarnings("WeakerAccess")
  protected static void tearDownMongo() throws Exception {
    if (mongodExecutable != null) {
      mongodExecutable.stop();
    }
    mongodExecutable = null;
  }
}
