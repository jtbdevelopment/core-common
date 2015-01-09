package com.jtbdevelopment.core.mongo.spring

import com.mongodb.WriteConcern
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.PostConstruct

/**
 * Date: 12/1/14
 * Time: 10:24 PM
 */
@Component
@CompileStatic
class MongoProperties {
    private static final Logger logger = LoggerFactory.getLogger(MongoProperties.class)

    @Value('${mongo.dbName:}')
    String dbName
    @Value('${mongo.host:localhost}')
    String dbHost
    @Value('${mongo.port:27017}')
    int dbPort
    @Value('${mongo.userName:}')
    String dbUser
    @Value('${mongo.userPassword:}')
    String dbPassword
    WriteConcern dbWriteConcern

    boolean warnings = true;

    @Value('${mongo.writeConcern:JOURNALED}')
    void setDbWriteConcern(final String dbWriteConcern) {
        this.dbWriteConcern = WriteConcern.valueOf(dbWriteConcern)
    }

    @PostConstruct
    void logInformation() {
        warnings = false
        logger.info("Connecting to mongo with host:port " + dbHost + ':' + dbPort)
        if (StringUtils.isEmpty(dbName)) {
            logger.warn("No mongo.dbName specified")
            warnings = true
        } else {
            logger.info('Connecting to ' + dbName)
        }
        logger.info("Using writeconcern = " + dbWriteConcern)
        if (StringUtils.isEmpty(dbPassword) && StringUtils.isEmpty(dbUser)) {
            //  OK even if not recommended
            logger.info('Using unauthenticated connection.')
        } else {
            logger.info('Using authenticated connection.')
            if (StringUtils.isEmpty(dbPassword) || StringUtils.isEmpty(dbUser)) {
                warnings = true
                logger.warn('-----------------------------------------------------')
                logger.warn('WARNING:  Connecting with a missing user or password.')
                logger.warn('-----------------------------------------------------')
            }
        }
    }
}
