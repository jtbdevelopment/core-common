package com.jtbdevelopment.core.hazelcast.sessions

import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import org.springframework.session.MapSession

/**
 * Date: 3/7/15
 * Time: 7:52 PM
 */
class HazelcastSessionMapConfigurerTest extends GroovyTestCase {
    void testModifyConfiguration() {
        MapConfig mapConfig
        def config = [
                addMapConfig: {
                    MapConfig mc ->
                        mapConfig = mc
                        return null
                }
        ] as Config
        new HazelcastSessionMapConfigurer().modifyConfiguration(config)
        assert mapConfig
        assert mapConfig.name == HazelcastSessionMapFactoryBean.MAP_NAME
        assert mapConfig.timeToLiveSeconds == MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS
    }
}
