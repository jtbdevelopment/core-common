package com.jtbdevelopment.core.mongo.spring
/**
 * Date: 1/9/15
 * Time: 6:36 PM
 */
class MongoPropertiesTest extends GroovyTestCase {

    void testNoWarningsOnPopulatedWithUser() {
        MongoProperties properties = new MongoProperties("db", "host", 32, 'user', 'pass', "ACKNOWLEDGED")
        assertFalse properties.warnings
    }

    void testWarningsOnPopulatedWithUserNoDB() {
        MongoProperties properties = new MongoProperties("", "host", 32, 'user', 'pass', "ACKNOWLEDGED")
        assert properties.warnings
    }

    void testWarningsOnPopulatedWithUserNoPassword() {
        MongoProperties properties = new MongoProperties("db", "host", 32, 'user', '', "ACKNOWLEDGED")
        assert properties.warnings
    }

    void testWarningsOnPopulatedWithPasswordAndNoUser() {
        MongoProperties properties = new MongoProperties("db", "host", 32, '', 'pass', "ACKNOWLEDGED")
        assert properties.warnings
    }

    void testNoWarningsOnPopulatedNoUser() {
        MongoProperties properties = new MongoProperties("db", "host", 32, '', '', "ACKNOWLEDGED")
        assertFalse properties.warnings
    }

    void testWarningsOnPopulatedNoUserAndNoDB() {
        MongoProperties properties = new MongoProperties("", "host", 32, '', '', "ACKNOWLEDGED")
        assert properties.warnings
    }
}
