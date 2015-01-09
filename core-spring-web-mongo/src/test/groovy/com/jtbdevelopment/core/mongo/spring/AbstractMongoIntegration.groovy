package com.jtbdevelopment.core.mongo.spring

import com.jtbdevelopment.core.mongo.spring.security.rememberme.MongoPersistentTokenRepository
import com.mongodb.DB
import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.junit.AfterClass
import org.junit.BeforeClass
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Date: 1/4/2015
 * Time: 7:14 PM
 */
abstract class AbstractMongoIntegration {
    protected static final String DB_NAME = "test"
    protected static final int DB_PORT = 13051

    protected static ApplicationContext context
    protected static MongoPersistentTokenRepository repository
    protected static MongodExecutable mongodExecutable = null
    protected static MongoClient mongoClient
    protected static DB db


    @SuppressWarnings("GroovyUnusedDeclaration")
    @BeforeClass
    static void setupMongo() throws Exception {
        if (mongodExecutable) return

        MongodStarter starter = MongodStarter.getDefaultInstance();

        int port = DB_PORT;
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();

        System.setProperty('mongo.port', DB_PORT.toString())
        System.setProperty('mongo.dbName', DB_NAME)
        System.setProperty('mongo.writeConcern', 'ACKNOWLEDGED')
        context = new AnnotationConfigApplicationContext("com.jtbdevelopment")
        repository = context.getBean(MongoPersistentTokenRepository.class)
        mongoClient = new MongoClient("localhost", DB_PORT);
        db = mongoClient.getDB(DB_NAME);
    }

    @AfterClass
    @SuppressWarnings("GroovyUnusedDeclaration")
    static void tearDownMongo() throws Exception {
        if (mongodExecutable != null)
            mongodExecutable.stop();
        mongodExecutable = null
    }

}
