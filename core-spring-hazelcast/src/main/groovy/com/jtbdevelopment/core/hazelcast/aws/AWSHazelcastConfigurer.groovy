package com.jtbdevelopment.core.hazelcast.aws

import com.hazelcast.config.AwsConfig
import com.hazelcast.config.Config
import com.hazelcast.config.JoinConfig
import com.hazelcast.config.NetworkConfig
import com.jtbdevelopment.core.hazelcast.HazelcastConfigurer
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 3/7/15
 * Time: 9:26 PM
 */
@Component
@CompileStatic
class AWSHazelcastConfigurer implements HazelcastConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(AWSHazelcastConfigurer.class)
    @Autowired(required = false)
    AWSClusterSettings awsClusterSettings

    @Override
    void modifyConfiguration(final Config config) {
        if (awsClusterSettings && awsClusterSettings.validAWSCluster) {
            logger.info('Initializing Hazelcast with AWS Cluster Config')
            AwsConfig awsConfig = new AwsConfig()
            awsConfig.accessKey = awsClusterSettings.awsAccessKey
            awsConfig.secretKey = awsClusterSettings.awsSecretKey
            awsConfig.region = awsClusterSettings.region.name
            awsConfig.enabled = true
            awsConfig.tagKey = awsClusterSettings.clusterKey
            awsConfig.tagValue = awsClusterSettings.clusterValue

            JoinConfig joinConfig = new JoinConfig()
            joinConfig.awsConfig = awsConfig
            joinConfig.multicastConfig.enabled = false
            joinConfig.tcpIpConfig.enabled = false
            NetworkConfig networkConfig = new NetworkConfig()
            networkConfig.join = joinConfig
            config.networkConfig = networkConfig
        }
    }
}
