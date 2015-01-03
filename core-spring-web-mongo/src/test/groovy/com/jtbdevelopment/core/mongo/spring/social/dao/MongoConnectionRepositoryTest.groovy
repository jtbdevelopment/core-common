package com.jtbdevelopment.core.mongo.spring.social.dao

/**
 * Date: 1/3/2015
 * Time: 11:59 AM
 */
class MongoConnectionRepositoryTest extends GroovyTestCase {
    private String id = 'TESTID'
    private MongoConnectionRepository repository = new MongoConnectionRepository(id)
    void testConstructor() {
        assert repository.userId == id
    }

    void testCreateSocialConnection() {
        assert repository.createSocialConnection() instanceof MongoSocialConnection
    }
}
