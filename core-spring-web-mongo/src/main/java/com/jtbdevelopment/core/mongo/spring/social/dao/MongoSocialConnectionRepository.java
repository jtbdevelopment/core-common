package com.jtbdevelopment.core.mongo.spring.social.dao;

import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnectionRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/**
 * Date: 12/30/2014 Time: 3:42 PM
 */
@Repository
public interface MongoSocialConnectionRepository extends
    AbstractSocialConnectionRepository<ObjectId, MongoSocialConnection> {

}
