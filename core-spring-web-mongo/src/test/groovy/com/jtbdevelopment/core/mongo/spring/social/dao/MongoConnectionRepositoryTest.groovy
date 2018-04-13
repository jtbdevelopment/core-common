package com.jtbdevelopment.core.mongo.spring.social.dao

import org.springframework.test.util.ReflectionTestUtils

/**
 * Date: 1/3/2015
 * Time: 11:59 AM
 */
class MongoConnectionRepositoryTest extends GroovyTestCase {
    private String id = 'TESTID'
    private MongoConnectionRepository repository = new MongoConnectionRepository(null, null, null, id)

    void testConstructor() {
        assert id == ReflectionTestUtils.getField(repository, "userId")
    }

    void testCreateSocialConnection() {
        assert repository.createSocialConnection() instanceof MongoSocialConnection
    }
}
