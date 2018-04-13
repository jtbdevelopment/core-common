package com.jtbdevelopment.core.hazelcast.group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 11/25/16 Time: 6:51 AM
 *
 * Use some common values to distinguish group
 */
@Component
public class GroupProperties {

  private static final Logger logger = LoggerFactory.getLogger(GroupProperties.class);
  private String groupSetting;

  //  TODO - add spring value test
  public GroupProperties(
      @Value("${mongo.dbName:}") final String mongoDbName,
      @Value("${facebook.clientID:}") final String facebookClientID,
      @Value("${hazelcast.group:}") final String groupSetting
  ) {
    this.groupSetting = groupSetting;
    if (StringUtils.isEmpty(this.groupSetting)) {
      this.groupSetting = mongoDbName;
    }
    if (StringUtils.isEmpty(this.groupSetting)) {
      this.groupSetting = facebookClientID;
    }
    logger.info("hazelcast group set to " + groupSetting);
  }

  public String getGroupSetting() {
    return groupSetting;
  }
}
