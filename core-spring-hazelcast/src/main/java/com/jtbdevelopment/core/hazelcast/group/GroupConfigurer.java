package com.jtbdevelopment.core.hazelcast.group;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.jtbdevelopment.core.hazelcast.HazelcastConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 11/25/16
 * Time: 6:46 AM
 */
@Component
public class GroupConfigurer implements HazelcastConfigurer {

    private final GroupProperties groupProperties;

    public GroupConfigurer(GroupProperties groupProperties) {
        this.groupProperties = groupProperties;
    }

    public void modifyConfiguration(final Config config) {
        if (!StringUtils.isEmpty(groupProperties.getGroupSetting())) {
            config.setGroupConfig(new GroupConfig(groupProperties.getGroupSetting()));
        }

    }
}
