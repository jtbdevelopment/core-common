package com.jtbdevelopment.core.hazelcast.group

import com.hazelcast.config.Config
import com.hazelcast.config.GroupConfig

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 11/25/16
 * Time: 6:47 AM
 */
class GroupConfigurerTest extends GroovyTestCase {
    GroupProperties groupProperties = mock(GroupProperties.class)
    GroupConfigurer configurer = new GroupConfigurer(groupProperties)

    void testModifyConfigurationWithNoGroupSetting() {
        Config config = new Config()
        GroupConfig originalConfig = config.groupConfig

        when(groupProperties.groupSetting).thenReturn(null)
        configurer.modifyConfiguration(config)

        assert originalConfig.is(config.groupConfig)
    }

    void testModifyConfigurationWithGroupSetting() {
        Config config = new Config()
        GroupConfig originalConfig = config.groupConfig

        when(groupProperties.groupSetting).thenReturn('Group!')
        configurer.modifyConfiguration(config)

        assertFalse originalConfig.is(config.groupConfig)
        assert 'Group!' == config.groupConfig.getName()
    }
}
