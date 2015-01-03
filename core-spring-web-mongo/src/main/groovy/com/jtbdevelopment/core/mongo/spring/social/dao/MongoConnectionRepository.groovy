package com.jtbdevelopment.core.mongo.spring.social.dao

import com.jtbdevelopment.core.spring.social.dao.AbstractConnectionRepository
import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnection
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.social.connect.ConnectionData

/**
 * Date: 12/16/14
 * Time: 1:17 PM
 */
@CompileStatic
class MongoConnectionRepository extends AbstractConnectionRepository {
    private static Logger logger = LoggerFactory.getLogger(MongoConnectionRepository.class)

    MongoConnectionRepository(final String userId) {
        super(userId)
    }

    @Override
    AbstractSocialConnection createSocialConnection() {
        return new MongoSocialConnection()
    }
}
