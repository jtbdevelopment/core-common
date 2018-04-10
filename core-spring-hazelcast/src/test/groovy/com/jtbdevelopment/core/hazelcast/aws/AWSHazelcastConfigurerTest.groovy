package com.jtbdevelopment.core.hazelcast.aws

import com.amazonaws.regions.RegionUtils
import com.amazonaws.regions.Regions
import com.hazelcast.config.AwsConfig
import com.hazelcast.config.Config
import com.hazelcast.config.JoinConfig
import com.hazelcast.config.NetworkConfig

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 3/14/15
 * Time: 1:46 PM
 */
class AWSHazelcastConfigurerTest extends GroovyTestCase {
    AWSClusterSettings clusterSettings = mock(AWSClusterSettings.class)
    AWSHazelcastConfigurer configurer = new AWSHazelcastConfigurer(clusterSettings)

    void testModifyConfigurationNullSettings() {
        configurer = new AWSHazelcastConfigurer(null)
        configurer.modifyConfiguration([] as Config)
    }

    void testModifyConfigurationInvalidSettings() {
        when(clusterSettings.validAWSCluster).thenReturn(false)
        configurer.modifyConfiguration([] as Config)
    }

    void testModifyConfigurationValidSettings() {
        def awsKey = 'AWS'
        def awsSecret = 'SEC'
        def awsCKey = 'KEY'
        def awsCValue = 'VAL'
        def region = RegionUtils.getRegion(Regions.CN_NORTH_1.name)
        when(clusterSettings.validAWSCluster).thenReturn(true)
        when(clusterSettings.awsAccessKey).thenReturn(awsKey)
        when(clusterSettings.awsSecretKey).thenReturn(awsSecret)
        when(clusterSettings.clusterKey).thenReturn(awsCKey)
        when(clusterSettings.clusterValue).thenReturn(awsCValue)
        when(clusterSettings.region).thenReturn(region)
        NetworkConfig config

        configurer.modifyConfiguration([
                setNetworkConfig: {
                    NetworkConfig c ->
                        config = c
                        null
                }
        ] as Config)

        assert config
        JoinConfig joinConfig = config.getJoin()
        assertFalse joinConfig.multicastConfig.enabled
        assertFalse joinConfig.tcpIpConfig.enabled
        AwsConfig awsConfig = joinConfig.awsConfig
        assert awsConfig.enabled
        assert awsConfig.accessKey == awsKey
        assert awsConfig.secretKey == awsSecret
        assert awsConfig.region == region.name
        assert awsConfig.tagKey == awsCKey
        assert awsConfig.tagValue == awsCValue
    }
}
