package com.jtbdevelopment.core.hazelcast.aws

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.Reservation
import com.amazonaws.util.EC2MetadataUtils
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 3/14/15
 * Time: 1:28 PM
 */
@CompileStatic
@Component
class AWSUtils {
    private static final Logger logger = LoggerFactory.getLogger(AWSUtils.class)

    @Autowired
    AWSClusterSettings awsClusterSettings

    Region getCurrentRegion() {
        return Regions.getCurrentRegion()
    }

    EC2MetadataUtils.InstanceInfo getCurrentInstanceInfo() {
        return EC2MetadataUtils.instanceInfo
    }

    Instance getCurrentInstance() {
        AmazonEC2 client = new AmazonEC2Client(new BasicAWSCredentials(awsClusterSettings.awsAccessKey, awsClusterSettings.awsSecretKey))
        client.setRegion(awsClusterSettings.region)
        DescribeInstancesRequest request = new DescribeInstancesRequest()
        request.withFilters(new Filter("instance-id", [awsClusterSettings.instanceInfo.instanceId]))
        List<Reservation> reservations = client.describeInstances(request).reservations
        if (reservations.size() != 1) {
            logger.warn('Expected one AWS reservation - received ' + reservations)
            return null
        }
        List<Instance> instances = reservations[0].instances
        if (instances.size() != 1) {
            logger.warn('Expected one AWS instance - received ' + instances)
            return null
        }
        return instances[0]
    }
}
