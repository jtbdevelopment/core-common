package com.jtbdevelopment.core.hazelcast.sessions;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

/**
 * Date: 3/6/15 Time: 6:34 PM
 *
 * TODO - possibly unnecessary in current spring session
 */
@Component
public class HazelcastSessionMapFactoryBean implements FactoryBean<MapSessionRepository> {

  public static final String MAP_NAME = "springSessionRepository";
  private final MapSessionRepository mapSessionRepository;

  public HazelcastSessionMapFactoryBean(final HazelcastInstance hazelcastInstance) {
    IMap<String, Session> map = hazelcastInstance.getMap(MAP_NAME);
    mapSessionRepository = new MapSessionRepository(map);
  }

  @Override
  public MapSessionRepository getObject() throws Exception {
    return mapSessionRepository;
  }

  @Override
  public Class<?> getObjectType() {
    return MapSessionRepository.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
