package com.jtbdevelopment.core.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15
 * Time: 9:02 AM
 */
@Component
public class HazelcastMembershipListener implements HazelcastConfigurer, MembershipListener {
    private static final Logger logger = LoggerFactory.getLogger(HazelcastMembershipListener.class);

    @Override
    public void modifyConfiguration(final Config config) {
        ListenerConfig listenerConfig = new ListenerConfig();
        listenerConfig.setImplementation(this);
        config.addListenerConfig(listenerConfig);
    }

    @Override
    public void memberAdded(final MembershipEvent membershipEvent) {
        logger.info("Hazelcast cluster event - " + membershipEvent.toString());
    }

    @Override
    public void memberRemoved(final MembershipEvent membershipEvent) {
        logger.info("Hazelcast cluster event - " + membershipEvent.toString());
    }

    @Override
    public void memberAttributeChanged(final MemberAttributeEvent memberAttributeEvent) {
        logger.info("Hazelcast cluster event - " + memberAttributeEvent.toString());
    }
}
