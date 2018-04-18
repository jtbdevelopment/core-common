package com.jtbdevelopment.core.hazelcast.group;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import org.junit.Test;

/**
 * Date: 11/25/16 Time: 6:47 AM
 */
public class GroupConfigurerTest {

  private GroupProperties groupProperties = mock(GroupProperties.class);
  private GroupConfigurer configurer = new GroupConfigurer(groupProperties);

  @Test
  public void testModifyConfigurationWithNoGroupSetting() {
    Config config = new Config();
    GroupConfig originalConfig = config.getGroupConfig();

    when(groupProperties.getGroupSetting()).thenReturn(null);
    configurer.modifyConfiguration(config);

    assertSame(originalConfig, config.getGroupConfig());
  }

  @Test
  public void testModifyConfigurationWithGroupSetting() {
    Config config = new Config();
    GroupConfig originalConfig = config.getGroupConfig();

    when(groupProperties.getGroupSetting()).thenReturn("Group!");
    configurer.modifyConfiguration(config);

    assertNotEquals(originalConfig, config.getGroupConfig());
    assertEquals("Group!", config.getGroupConfig().getName());
  }
}
