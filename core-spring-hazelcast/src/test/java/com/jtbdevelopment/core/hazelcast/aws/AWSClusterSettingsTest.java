package com.jtbdevelopment.core.hazelcast.aws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.util.EC2MetadataUtils.InstanceInfo;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 3/14/15 Time: 4:59 PM
 */
public class AWSClusterSettingsTest {

  private AWSUtils awsUtils = mock(AWSUtils.class);
  private AWSClusterSettings settings = new AWSClusterSettings(awsUtils);

  @Test
  public void testSetupNoKey() {
    settings.setup();
    assertFalse(settings.getValidAWSCluster());
  }

  @Test
  public void testSetupNoSecret() {
    ReflectionTestUtils.setField(settings, "awsAccessKey", "X");
    settings.setup();
    assertFalse(settings.getValidAWSCluster());
  }

  @Test
  public void testSetupNoRegion() {
    ReflectionTestUtils.setField(settings, "awsAccessKey", "X");
    ReflectionTestUtils.setField(settings, "awsSecretKey", "Y");
    when(awsUtils.getCurrentRegion()).thenReturn(null);
    settings.setup();
    assertFalse(settings.getValidAWSCluster());
  }

  @Test
  public void testSetupNoInstanceInfo() {
    ReflectionTestUtils.setField(settings, "awsAccessKey", "X");
    ReflectionTestUtils.setField(settings, "awsSecretKey", "Y");
    when(awsUtils.getCurrentRegion())
        .thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.getName()));
    when(awsUtils.getCurrentInstanceInfo()).thenReturn(null);
    settings.setup();
    assertFalse(settings.getValidAWSCluster());
  }

  @Test
  public void testSetupNoInstance() {
    ReflectionTestUtils.setField(settings, "awsAccessKey", "X");
    ReflectionTestUtils.setField(settings, "awsSecretKey", "Y");
    when(awsUtils.getCurrentRegion())
        .thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.getName()));
    when(awsUtils.getCurrentInstanceInfo()).thenReturn(
        new InstanceInfo(null, null, null, "II", null, null, null, null, null, null, null, null,
            null, null));
    when(awsUtils.getCurrentInstance(settings)).thenReturn(null);
    settings.setup();
    assertFalse(settings.getValidAWSCluster());
  }

  @Test
  public void testSetupFindsPrioritizedTag() {
    ReflectionTestUtils.setField(settings, "awsAccessKey", "X");
    ReflectionTestUtils.setField(settings, "awsSecretKey", "Y");
    ReflectionTestUtils.setField(settings, "tagsToSearch", Arrays.asList("first", "second"));
    when(awsUtils.getCurrentRegion())
        .thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.getName()));
    when(awsUtils.getCurrentInstanceInfo()).thenReturn(
        new InstanceInfo(null, null, null, "II", null, null, null, null, null, null, null, null,
            null, null));
    Instance instance = mock(Instance.class);
    when(awsUtils.getCurrentInstance(settings)).thenReturn(instance);
    when(instance.getTags())
        .thenReturn(Arrays.asList(new Tag("second", "2"), new Tag("first", "1")));
    settings.setup();
    assertTrue(settings.getValidAWSCluster());
    assertEquals("first", settings.getClusterKey());
    assertEquals("1", settings.getClusterValue());
  }

  @Test
  public void testSetupFindsAlternateTag() {
    ReflectionTestUtils.setField(settings, "awsAccessKey", "X");
    ReflectionTestUtils.setField(settings, "awsSecretKey", "Y");
    ReflectionTestUtils.setField(settings, "tagsToSearch", Arrays.asList("first", "second"));
    when(awsUtils.getCurrentRegion())
        .thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.getName()));
    when(awsUtils.getCurrentInstanceInfo()).thenReturn(
        new InstanceInfo(null, null, null, "II", null, null, null, null, null, null, null, null,
            null, null));
    Instance instance = mock(Instance.class);
    when(awsUtils.getCurrentInstance(settings)).thenReturn(instance);
    when(instance.getTags())
        .thenReturn(Arrays.asList(new Tag("second", "2"), new Tag("third", "3")));
    settings.setup();
    assertTrue(settings.getValidAWSCluster());
    assertEquals("second", settings.getClusterKey());
    assertEquals("2", settings.getClusterValue());
  }

  @Test
  public void testSetupFailsToAnyTag() {
    ReflectionTestUtils.setField(settings, "awsAccessKey", "X");
    ReflectionTestUtils.setField(settings, "awsSecretKey", "Y");
    ReflectionTestUtils.setField(settings, "tagsToSearch", Arrays.asList("first", "second"));
    when(awsUtils.getCurrentRegion())
        .thenReturn(RegionUtils.getRegion(Regions.CN_NORTH_1.getName()));
    when(awsUtils.getCurrentInstanceInfo()).thenReturn(
        new InstanceInfo(null, null, null, "II", null, null, null, null, null, null, null, null,
            null, null));
    Instance instance = mock(Instance.class);
    when(awsUtils.getCurrentInstance(settings)).thenReturn(instance);
    when(instance.getTags())
        .thenReturn(Arrays.asList(new Tag("fourth", "4"), new Tag("third", "3")));
    settings.setup();
    assertFalse(settings.getValidAWSCluster());
  }
}
