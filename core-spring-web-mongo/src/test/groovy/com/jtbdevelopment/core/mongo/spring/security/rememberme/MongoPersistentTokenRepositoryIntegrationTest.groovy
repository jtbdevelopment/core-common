package com.jtbdevelopment.core.mongo.spring.security.rememberme

import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookConnectionFactory
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterConnectionFactory
import com.mongodb.*
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.social.connect.support.ConnectionFactoryRegistry

/**
 * Date: 1/3/2015
 * Time: 9:51 PM
 */
class MongoPersistentTokenRepositoryIntegrationTest extends GroovyTestCase {
    public static final String DB_NAME = "test"
    public static final int DB_PORT = 13051

    ApplicationContext context
    MongoPersistentTokenRepository repository

    @SuppressWarnings("GroovyUnusedDeclaration")
    @Configuration
    @EnableMongoRepositories("com.jtbdevelopment")
    private static class IntegrationMongoConfiguration extends AbstractMongoConfiguration {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        @Autowired
        ConnectionFactoryRegistry connectionFactoryLocator() {
            ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
            registry.addConnectionFactory(new FakeFacebookConnectionFactory())
            registry.addConnectionFactory(new FakeTwitterConnectionFactory())
            return registry;
        }

        @Override
        protected String getDatabaseName() {
            return DB_NAME
        }

        @Override
        protected String getMappingBasePackage() {
            return "com.jtbdevelopment"
        }

        @Override
        Mongo mongo() throws Exception {
            MongoClient mongo = new MongoClient("localhost", DB_PORT);
            mongo.setWriteConcern(WriteConcern.JOURNALED)
            return mongo
        }
    }

    private static MongodExecutable mongodExecutable = null

    @Override
    protected void setUp() throws Exception {
        MongodStarter starter = MongodStarter.getDefaultInstance();

        int port = DB_PORT;
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();

        context = new AnnotationConfigApplicationContext("com.jtbdevelopment")
        repository = context.getBean(MongoPersistentTokenRepository.class)
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        if (mongodExecutable != null)
            mongodExecutable.stop();
    }

    public void testCollectionConfiguration() {
        MongoClient mongo = new MongoClient("localhost", DB_PORT);
        DB db = mongo.getDB(DB_NAME);
        assert db.collectionExists('rememberMeToken')
        DBCollection collection = db.getCollection('rememberMeToken')
        List<DBObject> indices = collection.indexInfo
        boolean seriesIndexFound = false
        indices.each {
            DBObject it ->
                switch (it.get('name')) {
                    case 'series':
                        seriesIndexFound = true
                        assert it.get('unique') == Boolean.TRUE
                        BasicDBObject key = it.get('key') as BasicDBObject
                        assert key.size() == 1
                        assert key.get('series') == 1
                        break;
                }
        }
        assert seriesIndexFound
    }
}
