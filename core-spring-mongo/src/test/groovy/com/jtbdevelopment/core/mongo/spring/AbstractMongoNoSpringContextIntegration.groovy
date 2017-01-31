package com.jtbdevelopment.core.mongo.spring

import com.mongodb.DB
import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network

/**
 * Date: 1/4/2015
 * Time: 7:14 PM
 */
abstract class AbstractMongoNoSpringContextIntegration {
    protected static final String DB_NAME = "test"
    protected static final int DB_PORT = 13051

    protected static MongodExecutable mongodExecutable = null
    protected static MongoClient mongoClient
    protected static DB db


    static void setupMongo() throws Exception {
        if (mongodExecutable) return

        MongodStarter starter = MongodStarter.getDefaultInstance()

        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(DB_PORT, Network.localhostIsIPv6()))
                .build()

        mongodExecutable = starter.prepare(mongodConfig)
        mongodExecutable.start()

        System.setProperty('mongo.port', DB_PORT.toString())
        System.setProperty('mongo.dbName', DB_NAME)
        System.setProperty('mongo.writeConcern', 'ACKNOWLEDGED')
        mongoClient = new MongoClient("localhost", DB_PORT)
        db = mongoClient.getDB(DB_NAME)
    }

    static void tearDownMongo() throws Exception {
        if (mongodExecutable != null)
            mongodExecutable.stop()
        mongodExecutable = null
    }

}
