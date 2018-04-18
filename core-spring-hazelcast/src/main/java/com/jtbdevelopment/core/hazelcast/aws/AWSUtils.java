package com.jtbdevelopment.core.hazelcast.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.util.EC2MetadataUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 3/14/15
 * Time: 1:28 PM
 */
@Component
public class AWSUtils {

  private static final Logger logger = LoggerFactory.getLogger(AWSUtils.class);

  public Region getCurrentRegion() {
    return Regions.getCurrentRegion();
  }

  public EC2MetadataUtils.InstanceInfo getCurrentInstanceInfo() {
    return EC2MetadataUtils.getInstanceInfo();
  }

  public Instance getCurrentInstance(final AWSClusterSettings awsClusterSettings) {
    AmazonEC2 client = new AmazonEC2Client(
        new BasicAWSCredentials(awsClusterSettings.getAwsAccessKey(),
            awsClusterSettings.getAwsSecretKey()));
    client.setRegion(awsClusterSettings.getRegion());
    DescribeInstancesRequest request = new DescribeInstancesRequest();
    request.withFilters(new Filter("instance-id", new ArrayList<>(
        Collections.singletonList(awsClusterSettings.getInstanceInfo().getInstanceId()))));
    List<Reservation> reservations = client.describeInstances(request).getReservations();
    if (reservations.size() != 1) {
      logger.warn("Expected one AWS reservation - received " + reservations);
      return null;
    }

    List<Instance> instances = reservations.get(0).getInstances();
    if (instances.size() != 1) {
      logger.warn("Expected one AWS instance - received " + instances);
      return null;
    }

    return instances.get(0);
  }
}
