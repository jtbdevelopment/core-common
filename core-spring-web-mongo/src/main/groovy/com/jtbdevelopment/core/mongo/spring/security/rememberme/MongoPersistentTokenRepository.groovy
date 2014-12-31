package com.jtbdevelopment.core.mongo.spring.security.rememberme

import com.jtbdevelopment.core.spring.security.rememberme.AbstractPersistentTokenRepository
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.stereotype.Component

/**
 * Date: 12/26/14
 * Time: 9:08 AM
 */
@CompileStatic
@Component
class MongoPersistentTokenRepository extends AbstractPersistentTokenRepository<ObjectId, MongoRememberMeToken> {
    @Override
    MongoRememberMeToken newToken(final PersistentRememberMeToken source) {
        return new MongoRememberMeToken(source)
    }

    @Override
    MongoRememberMeToken newToken(
            final ObjectId objectId,
            final String username, final String series, final String tokenValue, final Date date) {
        return new MongoRememberMeToken(username, series, tokenValue, date, objectId)
    }
}
