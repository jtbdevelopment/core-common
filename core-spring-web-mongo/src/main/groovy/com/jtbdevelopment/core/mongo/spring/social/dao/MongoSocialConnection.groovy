package com.jtbdevelopment.core.mongo.spring.social.dao

import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnection
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 12/16/14
 * Time: 1:04 PM
 */
@Document(collection = 'socialConnections')
@CompoundIndexes(
        [
                @CompoundIndex(name = "sc_uidpidc", unique = true, def = "{'userId': 1, 'providerId': 1, 'created': 1}"),
                @CompoundIndex(name = "sc_pk", unique = true, def = "{'userId': 1, 'providerId': 1, 'providerUserId': 1}"),
        ]
)
@CompileStatic
class MongoSocialConnection extends AbstractSocialConnection<ObjectId> {
    @Id
    ObjectId id  // generated internal
}
