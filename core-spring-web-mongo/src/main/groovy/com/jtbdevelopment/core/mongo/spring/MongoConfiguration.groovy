package com.jtbdevelopment.core.mongo.spring

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter
import com.mongodb.Mongo
import com.mongodb.MongoClient
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.authentication.UserCredentials
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.convert.CustomConversions
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.util.StringUtils

/**
 * Date: 1/9/15
 * Time: 6:38 AM
 */
@CompileStatic
@EnableMongoRepositories("com.jtbdevelopment")
@EnableMongoAuditing
@Configuration
class MongoConfiguration extends AbstractMongoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MongoConfiguration.class)

    @Autowired
    List<MongoConverter> mongoConverters

    @Autowired
    MongoProperties mongoProperties

    @Override
    CustomConversions customConversions() {
        return new CustomConversions(mongoConverters)
    }

    @Override
    protected String getMappingBasePackage() {
        return "com.jtbdevelopment"
    }

    @Override
    protected String getDatabaseName() {
        return mongoProperties.dbName
    }

    @Override
    protected UserCredentials getUserCredentials() {
        if (StringUtils.isEmpty(mongoProperties.dbPassword) || StringUtils.isEmpty(mongoProperties.dbUser)) {
            return null;
        }
        return new UserCredentials(mongoProperties.dbUser, mongoProperties.dbPassword)
    }

    //  Not unit testable
    @Override
    Mongo mongo() throws Exception {
        MongoClient mongo = new MongoClient(mongoProperties.dbHost, mongoProperties.dbPort);
        try {
            mongo.setWriteConcern(mongoProperties.dbWriteConcern)
        } catch (Exception e) {
            logger.warn("Unable to set Write Concern of " + mongoProperties.dbWriteConcern, e)
        }
        return mongo
    }

}
