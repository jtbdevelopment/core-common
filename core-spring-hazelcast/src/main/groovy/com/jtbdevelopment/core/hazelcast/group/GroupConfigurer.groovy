package com.jtbdevelopment.core.hazelcast.group

import com.hazelcast.config.Config
import com.hazelcast.config.GroupConfig
import com.jtbdevelopment.core.hazelcast.HazelcastConfigurer
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Date: 11/25/16
 * Time: 6:46 AM
 */
@CompileStatic
@Component
class GroupConfigurer implements HazelcastConfigurer {
    @Autowired
    GroupProperties groupProperties

    void modifyConfiguration(final Config config) {
        if (!StringUtils.isEmpty(groupProperties.groupSetting)) {
            config.groupConfig = new GroupConfig(groupProperties.groupSetting)
        }
    }
}
