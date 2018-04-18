package com.jtbdevelopment.core.hazelcast.sessions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.session.MapSession;

/**
 * Date: 3/7/15 Time: 7:52 PM
 */
public class HazelcastSessionMapConfigurerTest {

  @Test
  public void testModifyConfiguration() {
    Config config = mock(Config.class);
    new HazelcastSessionMapConfigurer().modifyConfiguration(config);
    ArgumentCaptor<MapConfig> captor = ArgumentCaptor.forClass(MapConfig.class);
    verify(config).addMapConfig(captor.capture());

    MapConfig configUsed = captor.getValue();
    assertNotNull(configUsed);
    assertEquals(HazelcastSessionMapFactoryBean.MAP_NAME, configUsed.getName());
    assertEquals(MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS,
        configUsed.getTimeToLiveSeconds());
  }

}
