package com.jtbdevelopment.core.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

/**
 * Date: 2/25/15 Time: 6:45 AM
 */
@Component
public class HazelcastInstanceFactoryBean implements FactoryBean<HazelcastInstance>, Lifecycle {

  private HazelcastInstance instance;
  private final List<HazelcastConfigurer> configurers;

  public HazelcastInstanceFactoryBean(final List<HazelcastConfigurer> configurers) {
    this.configurers = configurers;
  }

  @PostConstruct
  public void setup() {
  }

  @Override
  public HazelcastInstance getObject() {
    return instance;
  }

  @Override
  public Class<?> getObjectType() {
    return HazelcastInstance.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public void start() {
    final Config config = new Config();
    if (configurers != null) {
      configurers.forEach(c -> c.modifyConfiguration(config));
    }
    instance = Hazelcast.newHazelcastInstance(config);
  }

  @Override
  public void stop() {
    Hazelcast.shutdownAll();
    instance = null;
  }

  @Override
  public boolean isRunning() {
    return instance != null;
  }

}
