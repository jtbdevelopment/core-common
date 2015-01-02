package com.jtbdevelopment.core.mongo.spring.security.rememberme

import org.bson.types.ObjectId
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken

/**
 * Date: 1/2/15
 * Time: 6:02 PM
 */
class MongoRememberMeTokenTest extends GroovyTestCase {
    void testNewTokenFromExistingGenericToken() {
        PersistentRememberMeToken from = new PersistentRememberMeToken("u", "s", "t", new Date())
        MongoRememberMeToken to = new MongoRememberMeToken(from)

        assertNull to.id
        assert to.username == from.username
        assert to.series == from.series
        assert to.tokenValue == from.tokenValue
        assert to.date == from.date
    }

    void testNewTokenFromValues() {
        String s = "s"
        String tv = "t"
        String u = "u"
        Date d = new Date()
        ObjectId id = new ObjectId()

        MongoRememberMeToken t = new MongoRememberMeToken(u, s, tv, d, id)
        assert t.username == u
        assert t.series == s
        assert t.tokenValue == tv
        assert t.date == d
        assert t.id == id
    }
}
