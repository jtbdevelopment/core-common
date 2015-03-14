package com.jtbdevelopment.core.hazelcast.aws

import com.amazonaws.regions.RegionUtils
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.util.EC2MetadataUtils

/**
 * Date: 3/14/15
 * Time: 4:59 PM
 */
class AWSClusterSettingsTest extends GroovyTestCase {
    AWSClusterSettings settings = new AWSClusterSettings()

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
        settings.awsUtils = [
                getCurrentRegion: {
                    null
                }
        ] as AWSUtils
        settings.setup()
        assertFalse settings.validAWSCluster
    }

    void testSetupNoInstanceInfo() {
        settings.awsAccessKey = 'X'
        settings.awsSecretKey = 'Y'
        settings.awsUtils = [
                getCurrentRegion      : {
                    RegionUtils.getRegion(Regions.CN_NORTH_1.name)
                },
                getCurrentInstanceInfo: {
                    null
                }
        ] as AWSUtils
        settings.setup()
        assertFalse settings.validAWSCluster
    }

    void testSetupNoInstance() {
        settings.awsAccessKey = 'X'
        settings.awsSecretKey = 'Y'
        settings.awsUtils = [
                getCurrentRegion      : {
                    RegionUtils.getRegion(Regions.CN_NORTH_1.name)
                },
                getCurrentInstanceInfo: {
                    new EC2MetadataUtils.InstanceInfo(null, null, null, 'II', null, null, null, null, null, null, null, null, null, null)
                },
                getCurrentInstance    : {
                    null
                }
        ] as AWSUtils
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
        settings.awsUtils = [
                getCurrentRegion      : {
                    RegionUtils.getRegion(Regions.CN_NORTH_1.name)
                },
                getCurrentInstanceInfo: {
                    new EC2MetadataUtils.InstanceInfo(null, null, null, 'II', null, null, null, null, null, null, null, null, null, null)
                },
                getCurrentInstance    : {
                    [
                            getTags: {
                                Arrays.asList(new Tag('second', '2'), new Tag('first', '1'))
                            }
                    ] as Instance
                }
        ] as AWSUtils
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
        settings.awsUtils = [
                getCurrentRegion      : {
                    RegionUtils.getRegion(Regions.CN_NORTH_1.name)
                },
                getCurrentInstanceInfo: {
                    new EC2MetadataUtils.InstanceInfo(null, null, null, 'II', null, null, null, null, null, null, null, null, null, null)
                },
                getCurrentInstance    : {
                    [
                            getTags: {
                                Arrays.asList(new Tag('second', '2'), new Tag('third', '3'))
                            }
                    ] as Instance
                }
        ] as AWSUtils
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
        settings.awsUtils = [
                getCurrentRegion      : {
                    RegionUtils.getRegion(Regions.CN_NORTH_1.name)
                },
                getCurrentInstanceInfo: {
                    new EC2MetadataUtils.InstanceInfo(null, null, null, 'II', null, null, null, null, null, null, null, null, null, null)
                },
                getCurrentInstance    : {
                    [
                            getTags: {
                                Arrays.asList(new Tag('fourth', '4'), new Tag('third', '3'))
                            }
                    ] as Instance
                }
        ] as AWSUtils
        settings.setup()
        assertFalse settings.validAWSCluster
    }
}
