package com.jtbdevelopment.core.hazelcast.group;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 11/25/16
 * Time: 6:51 AM
 *
 * Use some common values to distinguish group
 */
@Component
public class GroupProperties {

  private static final Logger logger = LoggerFactory.getLogger(GroupProperties.class);
  @Value("${mongo.dbName:}")
  private String mongoDbName;
  @Value("${facebook.clientID:}")
  private String facebookClientID;
  @Value("${hazelcast.group:}")
  private String groupSetting;

  @PostConstruct
  public void setup() {
    if (StringUtils.isEmpty(groupSetting)) {
      if (!StringUtils.isEmpty(mongoDbName)) {
        groupSetting = mongoDbName;
      } else {
        if (!StringUtils.isEmpty(facebookClientID)) {
          groupSetting = facebookClientID;
        }

      }

    }

    logger.info("hazelcast group set to " + groupSetting);
  }

  public String getGroupSetting() {
    return groupSetting;
  }
}
