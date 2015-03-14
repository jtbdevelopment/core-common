package com.jtbdevelopment.core.hazelcast

import com.hazelcast.core.Hazelcast

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * Date: 3/8/15
 * Time: 6:39 PM
 */
@WebListener
class HazelcastShutdownListener implements ServletContextListener {
    @Override
    void contextInitialized(final ServletContextEvent sce) {

    }

    @Override
    void contextDestroyed(final ServletContextEvent sce) {
        Hazelcast.shutdownAll()
    }
}
