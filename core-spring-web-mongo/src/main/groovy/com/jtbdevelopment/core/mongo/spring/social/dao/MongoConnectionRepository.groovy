package com.jtbdevelopment.core.mongo.spring.social.dao

import com.jtbdevelopment.core.spring.social.dao.AbstractConnectionRepository
import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnection
import groovy.transform.CompileStatic

/**
 * Date: 12/16/14
 * Time: 1:17 PM
 */
@CompileStatic
class MongoConnectionRepository extends AbstractConnectionRepository {
    MongoConnectionRepository(final String userId) {
        super(userId)
    }

    @Override
    AbstractSocialConnection createSocialConnection() {
        return new MongoSocialConnection()
    }
}
