package com.jtbdevelopment.core.hazelcast.aws

import com.amazonaws.regions.RegionUtils
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.util.EC2MetadataUtils

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 3/14/15
 * Time: 4:59 PM
 */
class AWSClusterSettingsTest extends GroovyTestCase {
    AWSUtils awsUtils = mock(AWSUtils.class)
    AWSClusterSettings settings = new AWSClusterSettings(awsUtils)

    void testSetupNoKey() {
        settings.setup()
        assertFalse settings.validAWSCluster
    }

    void testSetupNoSecret() {
        settings.awsAccessKey = 'X'
        settings.setup()
        assertFalse settings.validAWSCluster
    }

    void testSetupNoRegion() {
        settings.awsAccessKey = 'X'
        settings.awsSecretKey = 'Y'
        when(awsUtils.currentRegion).thenReturn(null)
        settings.setup()
        assertFalse settings.validAWSCluster
    }

    void testSetupNoInstanceInfo() {
        settings.awsAccessKey = 'X'
        settings.awsSecretKey = 'Y'
        when(awsUtils.currentRegion).thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.name))
        when(awsUtils.currentInstanceInfo).thenReturn(null)
        settings.setup()
        assertFalse settings.validAWSCluster
    }

    void testSetupNoInstance() {
        settings.awsAccessKey = 'X'
        settings.awsSecretKey = 'Y'
        when(awsUtils.currentRegion).thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.name))
        when(awsUtils.currentInstanceInfo).thenReturn(new EC2MetadataUtils.InstanceInfo(null, null, null, 'II', null, null, null, null, null, null, null, null, null, null))
        when(awsUtils.getCurrentInstance(settings)).thenReturn(null)
        settings.setup()
        assertFalse settings.validAWSCluster
    }

    void testSetupFindsPrioritizedTag() {
        settings.awsAccessKey = 'X'
        settings.awsSecretKey = 'Y'
        settings.tagsToSearch = [
                'first',
                'second'
        ]
        when(awsUtils.currentRegion).thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.name))
        when(awsUtils.currentInstanceInfo).thenReturn(new EC2MetadataUtils.InstanceInfo(null, null, null, 'II', null, null, null, null, null, null, null, null, null, null))
        Instance instance = mock(Instance.class)
        when(awsUtils.getCurrentInstance(settings)).thenReturn(instance)
        when(instance.tags).thenReturn(Arrays.asList(new Tag('second', '2'), new Tag('first', '1')))
        settings.setup()
        assert settings.validAWSCluster
        assert settings.clusterKey == 'first'
        assert settings.clusterValue == '1'
    }

    void testSetupFindsAlternateTag() {
        settings.awsAccessKey = 'X'
        settings.awsSecretKey = 'Y'
        settings.tagsToSearch = [
                'first',
                'second'
        ]
        when(awsUtils.currentRegion).thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.name))
        when(awsUtils.currentInstanceInfo).thenReturn(new EC2MetadataUtils.InstanceInfo(null, null, null, 'II', null, null, null, null, null, null, null, null, null, null))
        Instance instance = mock(Instance.class)
        when(awsUtils.getCurrentInstance(settings)).thenReturn(instance)
        when(instance.tags).thenReturn(Arrays.asList(new Tag('second', '2'), new Tag('third', '3')))
        settings.setup()
        assert settings.validAWSCluster
        assert settings.clusterKey == 'second'
        assert settings.clusterValue == '2'
    }

    void testSetupFailsToAnyTag() {
        settings.awsAccessKey = 'X'
        settings.awsSecretKey = 'Y'
        settings.tagsToSearch = [
                'first',
                'second'
        ]
        when(awsUtils.currentRegion).thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.name))
        when(awsUtils.currentInstanceInfo).thenReturn(new EC2MetadataUtils.InstanceInfo(null, null, null, 'II', null, null, null, null, null, null, null, null, null, null))
        Instance instance = mock(Instance.class)
        when(awsUtils.getCurrentInstance(settings)).thenReturn(instance)
        when(instance.tags).thenReturn(Arrays.asList(new Tag('fourth', '4'), new Tag('third', '3')))
        settings.setup()
        assertFalse settings.validAWSCluster
    }
}
