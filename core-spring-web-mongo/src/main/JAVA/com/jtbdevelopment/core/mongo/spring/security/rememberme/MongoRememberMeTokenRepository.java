package com.jtbdevelopment.core.mongo.spring.security.rememberme;

import com.jtbdevelopment.core.spring.security.rememberme.AbstractRememberMeTokenRepository;
import org.bson.types.ObjectId;

/**
 * Date: 12/30/2014 Time: 3:42 PM
 */
public interface MongoRememberMeTokenRepository extends
    AbstractRememberMeTokenRepository<ObjectId, MongoRememberMeToken> {

}
