package com.jtbdevelopment.core.hazelcast.group;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 11/25/16 Time: 7:02 AM
 */
public class GroupPropertiesTest {

  @Test
  public void testWhenHazelcastGroupSet() {
    GroupProperties p = new GroupProperties("M", "FB", "HZ");
    Assert.assertEquals("HZ", p.getGroupSetting());
  }

  @Test
  public void testWhenHazelcastGroupNotSetAndMongoIs() {
    GroupProperties p = new GroupProperties("M", "FB", null);
    Assert.assertEquals("M", p.getGroupSetting());
  }

  @Test
  public void testWhenHazelcastGroupNotSetAndMongoIsNotAndFBIS() {
    GroupProperties p = new GroupProperties(null, "FB", "");
    Assert.assertEquals("FB", p.getGroupSetting());
  }

  @Test
  public void testWhenNothingIsSet() {
    GroupProperties p = new GroupProperties(null, "", "");
    Assert.assertEquals("", p.getGroupSetting());
  }

}
