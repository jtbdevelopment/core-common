package com.jtbdevelopment.core.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

/**
 * Date: 2/25/15 Time: 6:45 AM
 */
@Component
public class HazelcastInstanceFactoryBean implements FactoryBean<HazelcastInstance>, Lifecycle {

  private HazelcastInstance instance;
  private List<HazelcastConfigurer> configurers;

  @Autowired
  public HazelcastInstanceFactoryBean() {

  }

  @Autowired
  public void setConfigurers(final List<HazelcastConfigurer> configurers) {
    this.configurers = configurers;
  }

  @PostConstruct
  public synchronized void setup() {
    if (instance == null) {
      final Config config = new Config();
      if (configurers != null) {
        configurers.forEach(c -> c.modifyConfiguration(config));
      }
      instance = Hazelcast.newHazelcastInstance(config);
    }
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
    setup();
  }

  @Override
  public synchronized void stop() {
    Hazelcast.shutdownAll();
    instance = null;
  }

  @Override
  public boolean isRunning() {
    return instance != null;
  }

}
