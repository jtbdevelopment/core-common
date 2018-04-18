package com.jtbdevelopment.core.hazelcast.aws;

import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.jtbdevelopment.core.hazelcast.HazelcastConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15
 * Time: 9:26 PM
 */
@Component
public class AWSHazelcastConfigurer implements HazelcastConfigurer {

  private static final Logger logger = LoggerFactory.getLogger(AWSHazelcastConfigurer.class);
  private final AWSClusterSettings awsClusterSettings;

  public AWSHazelcastConfigurer(final AWSClusterSettings awsClusterSettings) {
    this.awsClusterSettings = awsClusterSettings;
  }

  @Override
  public void modifyConfiguration(final Config config) {
    if (awsClusterSettings != null && awsClusterSettings.getValidAWSCluster()) {
      logger.info("Initializing Hazelcast with AWS Cluster Config");
      AwsConfig awsConfig = new AwsConfig();
      awsConfig.setAccessKey(awsClusterSettings.getAwsAccessKey());
      awsConfig.setSecretKey(awsClusterSettings.getAwsSecretKey());
      awsConfig.setRegion(awsClusterSettings.getRegion().getName());
      awsConfig.setEnabled(true);
      awsConfig.setTagKey(awsClusterSettings.getClusterKey());
      awsConfig.setTagValue(awsClusterSettings.getClusterValue());

      JoinConfig joinConfig = new JoinConfig();
      joinConfig.setAwsConfig(awsConfig);
      joinConfig.getMulticastConfig().setEnabled(false);
      joinConfig.getTcpIpConfig().setEnabled(false);
      NetworkConfig networkConfig = new NetworkConfig();
      networkConfig.setJoin(joinConfig);
      config.setNetworkConfig(networkConfig);
    }

  }
}
