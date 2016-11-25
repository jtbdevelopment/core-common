package com.jtbdevelopment.core.hazelcast.group

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.PostConstruct

/**
 * Date: 11/25/16
 * Time: 6:51 AM
 *
 * Use some common values to distinguish group
 */
@Component
@CompileStatic
class GroupProperties {
    private static final Logger logger = LoggerFactory.getLogger(GroupProperties.class)
    @Value('${mongo.dbName:}')
    String mongoDbName

    @Value('${facebook.clientID:}')
    String facebookClientID

    @Value('${hazelcast.group:}')
    String groupSetting

    @PostConstruct
    void setup() {
        if (StringUtils.isEmpty(groupSetting)) {
            if (!StringUtils.isEmpty(mongoDbName)) {
                groupSetting = mongoDbName;
            } else {
                if (!StringUtils.isEmpty(facebookClientID)) {
                    groupSetting = facebookClientID;
                }
            }
        }
        logger.info('hazelcast group set to ' + groupSetting)
    }
}
