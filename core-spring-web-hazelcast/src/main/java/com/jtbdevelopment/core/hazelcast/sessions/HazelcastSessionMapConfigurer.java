package com.jtbdevelopment.core.hazelcast.sessions;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.jtbdevelopment.core.hazelcast.HazelcastConfigurer;
import org.springframework.session.MapSession;
import org.springframework.stereotype.Component;

/**
 * Date: 3/6/15 Time: 10:03 PM
 */
@Component
public class HazelcastSessionMapConfigurer implements HazelcastConfigurer {

  @Override
  public void modifyConfiguration(final Config config) {
    MapConfig mc = new MapConfig(HazelcastSessionMapFactoryBean.MAP_NAME);
    mc.setTimeToLiveSeconds(MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS);
    config.addMapConfig(mc);
  }

}
