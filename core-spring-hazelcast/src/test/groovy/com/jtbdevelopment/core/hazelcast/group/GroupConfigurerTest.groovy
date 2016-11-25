package com.jtbdevelopment.core.hazelcast.group

import com.hazelcast.config.Config
import com.hazelcast.config.GroupConfig

/**
 * Date: 11/25/16
 * Time: 6:47 AM
 */
class GroupConfigurerTest extends GroovyTestCase {
    GroupConfigurer configurer = new GroupConfigurer()

    void testModifyConfigurationWithNoGroupSetting() {
        Config config = new Config()
        GroupConfig originalConfig = config.groupConfig

        configurer.groupProperties = new GroupProperties(groupSetting: null)
        configurer.modifyConfiguration(config)

        assert originalConfig.is(config.groupConfig)
    }

    void testModifyConfigurationWithGroupSetting() {
        Config config = new Config()
        GroupConfig originalConfig = config.groupConfig

        configurer.groupProperties = new GroupProperties(groupSetting: 'Group!')
        configurer.modifyConfiguration(config)

        assertFalse originalConfig.is(config.groupConfig)
        assert 'Group!' == config.groupConfig.getName()
    }
}
