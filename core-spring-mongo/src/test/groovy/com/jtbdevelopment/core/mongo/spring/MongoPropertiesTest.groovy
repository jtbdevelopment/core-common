package com.jtbdevelopment.core.mongo.spring

import com.mongodb.WriteConcern
import org.springframework.beans.factory.annotation.Value

/**
 * Date: 1/9/15
 * Time: 6:36 PM
 */
class MongoPropertiesTest extends GroovyTestCase {
    MongoProperties properties = new MongoProperties()

    void testSetDbWriteConcern() {
        assertNull properties.dbWriteConcern
        properties.setDbWriteConcern("JOURNALED")
        assert properties.dbWriteConcern == WriteConcern.JOURNALED
        properties.setDbWriteConcern("FSYNC_SAFE")
        assert properties.dbWriteConcern == WriteConcern.FSYNC_SAFE
        properties.setDbWriteConcern("ACKNOWLEDGED")
        assert properties.dbWriteConcern == WriteConcern.ACKNOWLEDGED
    }

    void testSetDbWriteConcernAnnotations() {
        assert MongoProperties.class.getMethod('setDbWriteConcern', [String.class] as Class[]).getAnnotation(Value.class).value() == '${mongo.writeConcern:JOURNALED}'
    }

    void testFieldProperties() {
        def expected = [
                'dbName'    : '${mongo.dbName:}',
                'dbHost'    : '${mongo.host:localhost}',
                'dbPort'    : '${mongo.port:27017}',
                'dbUser'    : '${mongo.userName:}',
                'dbPassword': '${mongo.userPassword:}'
        ]
        expected.each {
            String field, String value ->
                MongoProperties.getDeclaredField(field).getAnnotation(Value.class).value() == value
        }
    }

    void testNoWarningsOnPopulatedWithUser() {
        assert properties.warnings
        properties.dbName = 'X'
        properties.dbUser = 'Y'
        properties.dbPassword = 'Z'
        properties.logInformation()
        assertFalse properties.warnings
    }

    void testWarningsOnPopulatedWithUserNoDB() {
        assert properties.warnings
        properties.dbName = ''
        properties.dbUser = 'Y'
        properties.dbPassword = 'Z'
        properties.logInformation()
        assert properties.warnings
    }

    void testWarningsOnPopulatedWithUserNoPassword() {
        assert properties.warnings
        properties.dbName = 'X'
        properties.dbUser = 'Y'
        properties.dbPassword = ''
        properties.logInformation()
        assert properties.warnings
    }

    void testWarningsOnPopulatedWithPasswordAndNoUser() {
        assert properties.warnings
        properties.dbName = 'X'
        properties.dbUser = null
        properties.dbPassword = 'Z'
        properties.logInformation()
        assert properties.warnings
    }

    void testNoWarningsOnPopulatedNoUser() {
        assert properties.warnings
        properties.dbName = 'X'
        properties.logInformation()
        assertFalse properties.warnings
    }

    void testWarningsOnPopulatedNoUserAndNoDB() {
        assert properties.warnings
        properties.dbName = null
        properties.logInformation()
        assert properties.warnings
    }
}
