package com.jtbdevelopment.core.hazelcast;

import com.hazelcast.config.Config;

/**
 * Date: 3/6/15
 * Time: 6:56 PM
 */
public interface HazelcastConfigurer {
    void modifyConfiguration(final Config config);
}
