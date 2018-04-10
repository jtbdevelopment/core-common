package com.jtbdevelopment.core.hazelcast.aws;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.util.EC2MetadataUtils;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 3/14/15
 * Time: 12:55 PM
 */
@Component
public class AWSClusterSettings {

  private static final Logger logger = LoggerFactory.getLogger(AWSClusterSettings.class);
  private final AWSUtils awsUtils;
  @Value("${AWS_ACCESS_KEY_ID:}")
  private String awsAccessKey;
  @Value("${AWS_SECRET_KEY:}")
  private String awsSecretKey;
  @Value("${aws.orderTagSearch:elasticbeanstalk:environment-name,Name}")
  private List<String> tagsToSearch;
  private String clusterKey;
  private String clusterValue;
  private Region region;
  private EC2MetadataUtils.InstanceInfo instanceInfo;
  private boolean validAWSCluster = false;

  public AWSClusterSettings(final AWSUtils awsUtils) {
    this.awsUtils = awsUtils;
  }

  @PostConstruct
  public void setup() {
    try {
      if (hasValidAccessDetails() && hasValidRegion() && hasValidInstanceInfo()
          && hasValidAccessDetails()) {
        validAWSCluster = foundAClusterNameInTags();
        if (validAWSCluster) {
          logger.info(
              "Found a valid AWS cluster setup in region " + region + ", with key \"" + clusterKey
                  + "\" and value \"" + clusterValue + "\"");
        }

      }

    } catch (Exception e) {
      logger.error("Error trying to determine AWS Cluster Settings", e);
      validAWSCluster = false;
    }

  }

  private boolean foundAClusterNameInTags() {
    Instance instance = awsUtils.getCurrentInstance(this);
    if (instance != null) {
      List<Tag> filteredTags = instance.getTags()
          .stream()
          .filter(x -> tagsToSearch.contains(x.getKey()))
          .sorted(Comparator.comparingInt(a -> tagsToSearch.indexOf(a.getKey())))
          .collect(Collectors.toList());

      if (filteredTags.isEmpty()) {
        logger.warn("No tags were found in search list");
        return false;
      }

      clusterKey = filteredTags.get(0).getKey();
      clusterValue = filteredTags.get(0).getValue();
    }

    return (!StringUtils.isEmpty(clusterKey)) && (!StringUtils.isEmpty(clusterValue));
  }

  private boolean hasValidRegion() {
    region = awsUtils.getCurrentRegion();
    return (region != null);
  }

  private boolean hasValidInstanceInfo() {
    instanceInfo = awsUtils.getCurrentInstanceInfo();
    return (instanceInfo != null);
  }

  private boolean hasValidAccessDetails() {
    if (StringUtils.isEmpty(awsAccessKey)) {
      logger.info("No AWS access key - skipping AWS configuration");
      return false;
    }

    if (StringUtils.isEmpty(awsSecretKey)) {
      logger.info("No AWS secret key - skipping AWS configuration");
      return false;
    }

    return true;
  }

  public String getAwsAccessKey() {
    return awsAccessKey;
  }

  public String getAwsSecretKey() {
    return awsSecretKey;
  }

  public String getClusterKey() {
    return clusterKey;
  }

  public String getClusterValue() {
    return clusterValue;
  }

  public Region getRegion() {
    return region;
  }

  public EC2MetadataUtils.InstanceInfo getInstanceInfo() {
    return instanceInfo;
  }

  public boolean getValidAWSCluster() {
    return validAWSCluster;
  }
}
