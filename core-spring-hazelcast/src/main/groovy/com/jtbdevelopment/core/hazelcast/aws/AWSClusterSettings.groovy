package com.jtbdevelopment.core.hazelcast.aws

import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.util.EC2MetadataUtils
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.PostConstruct

/**
 * Date: 3/14/15
 * Time: 12:55 PM
 */
@Component
@CompileStatic
class AWSClusterSettings {
    private static final Logger logger = LoggerFactory.getLogger(AWSClusterSettings.class)
    @Autowired
    AWSUtils awsUtils

    @Value('${AWS_ACCESS_KEY_ID:}')
    String awsAccessKey
    @Value('${AWS_SECRET_KEY:}')
    String awsSecretKey
    @Value('${aws.orderTagSearch:elasticbeanstalk:environment-name,Name}')
    String[] tagsToSearch

    String clusterKey
    String clusterValue

    Region region
    EC2MetadataUtils.InstanceInfo instanceInfo

    boolean validAWSCluster = false

    @PostConstruct
    void setup() {
        try {
            if (hasValidAccessDetails() && hasValidRegion() && hasValidAccessDetails()) {
                validAWSCluster = foundAClusterNameInTags()
                if (validAWSCluster) {
                    logger.info('Found a valid AWS cluster setup in region ' + region + ', with key "' + clusterKey + '" and value "' + clusterValue + '"')
                }
            }
        } catch (Exception e) {
            logger.error('Error trying to determine AWS Cluster Settings', e)
            validAWSCluster = false
        }
    }

    protected boolean foundAClusterNameInTags() {
        List<Tag> tags = awsUtils.getCurrentInstance().tags
        List<Tag> ordered = []
        tagsToSearch.each {
            String tag ->
                Tag found = tags.find { Tag ec2tag -> ec2tag.key == tag }
                if (found) ordered.add(found)
        }
        if (ordered.empty) {
            logger.warn('No tags were found in order search list - received ' + tags + ' and searching for ' + tagsToSearch)
            return false
        }

        clusterKey = ordered[0].key
        clusterValue = ordered[0].value
        return (!StringUtils.isEmpty(clusterKey)) && (!StringUtils.isEmpty(clusterValue))
    }

    protected boolean hasValidRegion() {
        region = Regions.getCurrentRegion()
        return (region != null)
    }

    protected boolean hasValidInstanceInfo() {
        instanceInfo = EC2MetadataUtils.instanceInfo
        return (instanceInfo != null)
    }

    protected boolean hasValidAccessDetails() {
        if (StringUtils.isEmpty(awsAccessKey)) {
            logger.info('No AWS access key - skipping AWS configuration')
            return false
        }
        if (StringUtils.isEmpty(awsSecretKey)) {
            logger.info('No AWS secret key - skipping AWS configuration')
            return false
        }
        true
    }


}
