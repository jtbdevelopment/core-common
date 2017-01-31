package com.jtbdevelopment.core.mongo.spring

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter
import com.mongodb.Mongo
import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.convert.CustomConversions
import org.springframework.util.StringUtils

/**
 * Date: 1/9/15
 * Time: 6:38 AM
 */
@CompileStatic
class AbstractCoreMongoConfiguration extends AbstractMongoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCoreMongoConfiguration.class)

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

    //  Not unit testable
    @Override
    Mongo mongo() throws Exception {
        MongoClient mongo
        if (!StringUtils.isEmpty(mongoProperties.dbPassword) && !StringUtils.isEmpty(mongoProperties.dbUser)) {
            mongo = new MongoClient(
                    [new ServerAddress(mongoProperties.dbHost, mongoProperties.dbPort)],
                    [MongoCredential.createCredential(mongoProperties.dbUser, mongoProperties.dbName, mongoProperties.dbPassword.toCharArray())]);
        } else {
            mongo = new MongoClient(mongoProperties.dbHost, mongoProperties.dbPort);
        }
        try {
            mongo.setWriteConcern(mongoProperties.dbWriteConcern)
        } catch (Exception e) {
            logger.warn("Unable to set Write Concern of " + mongoProperties.dbWriteConcern, e)
        }
        return mongo
    }

}
