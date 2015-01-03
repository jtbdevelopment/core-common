package com.jtbdevelopment.core.mongo.spring.social.dao

import com.jtbdevelopment.core.mongo.spring.security.rememberme.MongoRememberMeToken
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

import java.lang.reflect.Field

/**
 * Date: 1/3/2015
 * Time: 12:00 PM
 */
class MongoSocialConnectionTest extends GroovyTestCase {
    void testClassAnnotations() {
        Document d = MongoSocialConnection.class.getAnnotation(Document.class)
        assert d
        assert d.collection() == 'socialConnections'
        CompoundIndexes ci = MongoSocialConnection.class.getAnnotation(CompoundIndexes.class)
        assert ci
        assert ci.value().length == 2
        CompoundIndex i = ci.value()[0]
        assert i.def() == "{'userId': 1, 'providerId': 1, 'created': 1}"
        assert i.unique()
        assert i.name() == "sc_uidpidc"
        i = ci.value()[1]
        assert i.def() == "{'userId': 1, 'providerId': 1, 'providerUserId': 1}"
        assert i.unique()
        assert i.name() == "sc_pk"
    }

    void testIdAnnotation() {
        Field m = MongoRememberMeToken.class.getDeclaredField('id')
        assert m
        Id i = m.getAnnotation(Id.class)
        assert i
    }
}
