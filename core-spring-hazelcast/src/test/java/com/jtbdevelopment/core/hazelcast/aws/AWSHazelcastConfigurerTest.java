package com.jtbdevelopment.core.hazelcast.aws;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 3/14/15 Time: 1:46 PM
 */
public class AWSHazelcastConfigurerTest {

  private AWSClusterSettings clusterSettings = Mockito.mock(AWSClusterSettings.class);
  private AWSHazelcastConfigurer configurer = new AWSHazelcastConfigurer(clusterSettings);
  private Config config = Mockito.mock(Config.class);

  @Test
  public void testModifyConfigurationNullSettings() {
    configurer = new AWSHazelcastConfigurer(null);
    configurer.modifyConfiguration(config);
    Mockito.verify(config, Mockito.never()).setNetworkConfig(Matchers.isA(NetworkConfig.class));
  }

  @Test
  public void testModifyConfigurationInvalidSettings() {
    Mockito.when(clusterSettings.getValidAWSCluster()).thenReturn(false);
    configurer.modifyConfiguration(config);
    Mockito.verify(config, Mockito.never()).setNetworkConfig(Matchers.isA(NetworkConfig.class));
  }

  @Test
  public void testModifyConfigurationValidSettings() {
    String awsKey = "AWS";
    String awsSecret = "SEC";
    String awsCKey = "KEY";
    String awsCValue = "VAL";
    Region region = RegionUtils.getRegion(Regions.CN_NORTH_1.getName());
    Mockito.when(clusterSettings.getValidAWSCluster()).thenReturn(true);
    Mockito.when(clusterSettings.getAwsAccessKey()).thenReturn(awsKey);
    Mockito.when(clusterSettings.getAwsSecretKey()).thenReturn(awsSecret);
    Mockito.when(clusterSettings.getClusterKey()).thenReturn(awsCKey);
    Mockito.when(clusterSettings.getClusterValue()).thenReturn(awsCValue);
    Mockito.when(clusterSettings.getRegion()).thenReturn(region);

    configurer.modifyConfiguration(config);
    ArgumentCaptor<NetworkConfig> captor = ArgumentCaptor.forClass(NetworkConfig.class);
    Mockito.verify(config).setNetworkConfig(captor.capture());

    NetworkConfig networkConfig = captor.getValue();
    Assert.assertNotNull(networkConfig);
    JoinConfig joinConfig = networkConfig.getJoin();
    Assert.assertFalse(joinConfig.getMulticastConfig().isEnabled());
    Assert.assertFalse(joinConfig.getTcpIpConfig().isEnabled());
    AwsConfig awsConfig = joinConfig.getAwsConfig();
    Assert.assertTrue(awsConfig.isEnabled());
    Assert.assertEquals(awsKey, awsConfig.getAccessKey());
    Assert.assertEquals(awsSecret, awsConfig.getSecretKey());
    Assert.assertEquals(awsCKey, awsConfig.getTagKey());
    Assert.assertEquals(awsCValue, awsConfig.getTagValue());
    Assert.assertEquals(region.getName(), awsConfig.getRegion());
  }
}
