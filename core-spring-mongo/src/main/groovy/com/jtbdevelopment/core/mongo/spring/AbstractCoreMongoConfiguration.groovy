package com.jtbdevelopment.core.mongo.spring

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.convert.CustomConversions
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.util.StringUtils

/**
 * Date: 1/9/15
 * Time: 6:38 AM
 */
@CompileStatic
class AbstractCoreMongoConfiguration extends AbstractMongoConfiguration {
    @Autowired
    List<MongoConverter> mongoConverters

    @Autowired
    MongoProperties mongoProperties

    @Override
    CustomConversions customConversions() {
        return new MongoCustomConversions(mongoConverters)
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
    MongoClient mongoClient() {
        MongoClientOptions options = MongoClientOptions.builder().writeConcern(mongoProperties.dbWriteConcern).build()
        if (!StringUtils.isEmpty(mongoProperties.dbPassword) && !StringUtils.isEmpty(mongoProperties.dbUser)) {
            return new MongoClient(
                    [new ServerAddress(mongoProperties.dbHost, mongoProperties.dbPort)],
                    [MongoCredential.createCredential(mongoProperties.dbUser, mongoProperties.dbName, mongoProperties.dbPassword.toCharArray())],
                    options
            )
        } else {
            return new MongoClient([new ServerAddress(mongoProperties.dbHost, mongoProperties.dbPort)], options)
        }
    }


}
