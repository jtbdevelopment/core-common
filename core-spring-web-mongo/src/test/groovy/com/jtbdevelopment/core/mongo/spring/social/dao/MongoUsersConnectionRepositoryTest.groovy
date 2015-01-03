package com.jtbdevelopment.core.mongo.spring.social.dao

import org.springframework.social.connect.ConnectionRepository

/**
 * Date: 1/3/2015
 * Time: 11:51 AM
 */
class MongoUsersConnectionRepositoryTest extends GroovyTestCase {
    MongoUsersConnectionRepository repository = new MongoUsersConnectionRepository()

    void testCreateConnectionRepository() {
        String anId = 'TADA!'
        ConnectionRepository r = repository.createConnectionRepository(anId)
        assert r instanceof MongoConnectionRepository
        assert r.userId == anId
    }

    void testCreateConnectionRepositoryWithNull() {
        shouldFail(IllegalArgumentException.class) {
            repository.createConnectionRepository(null)
        }
    }

    void testCreateConnectionRepositoryWithBlank() {
        shouldFail(IllegalArgumentException.class) {
            repository.createConnectionRepository('')
        }
    }
}
