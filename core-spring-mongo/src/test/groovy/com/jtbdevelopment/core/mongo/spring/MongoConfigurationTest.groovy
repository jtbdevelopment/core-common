package com.jtbdevelopment.core.mongo.spring

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * Date: 1/9/15
 * Time: 6:51 PM
 */
class MongoConfigurationTest extends GroovyTestCase {
    void testClassAnnotations() {
        assert MongoConfiguration.class.getAnnotation(Configuration.class)
        assert MongoConfiguration.class.getAnnotation(EnableMongoAuditing.class)
        assert MongoConfiguration.class.getAnnotation(EnableMongoRepositories.class).value() == ['com.jtbdevelopment']
    }
}
