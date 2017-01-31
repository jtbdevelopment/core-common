package com.jtbdevelopment.core.mongo.spring

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * Date: 1/9/15
 * Time: 6:38 AM
 */
@CompileStatic
@EnableMongoRepositories("com.jtbdevelopment")
@EnableMongoAuditing
@Configuration
class MongoConfiguration extends AbstractCoreMongoConfiguration {
}
