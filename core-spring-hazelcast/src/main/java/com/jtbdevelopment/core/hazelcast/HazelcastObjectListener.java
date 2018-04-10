package com.jtbdevelopment.core.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15
 * Time: 9:02 AM
 */
@Component
public class HazelcastObjectListener implements HazelcastConfigurer, DistributedObjectListener {
    private static final Logger logger = LoggerFactory.getLogger(HazelcastObjectListener.class);

    @Override
    public void modifyConfiguration(final Config config) {
        ListenerConfig listenerConfig = new ListenerConfig();
        listenerConfig.setImplementation(this);
        config.addListenerConfig(listenerConfig);
    }

    @Override
    public void distributedObjectCreated(final DistributedObjectEvent event) {
        logger.info("Hazelcast object event - " + event.toString());
    }

    @Override
    public void distributedObjectDestroyed(final DistributedObjectEvent event) {
        logger.info("Hazelcast object event - " + event.toString());
    }
}
