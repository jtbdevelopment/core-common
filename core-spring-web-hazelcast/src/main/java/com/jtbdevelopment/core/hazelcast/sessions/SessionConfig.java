package com.jtbdevelopment.core.hazelcast.sessions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;

/**
 * Date: 3/6/15 Time: 6:41 PM
 */
@Configuration
public class SessionConfig {

  private final MapSessionRepository mapSessionRepository;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  public SessionConfig(final MapSessionRepository mapSessionRepository) {
    this.mapSessionRepository = mapSessionRepository;
  }

  @Bean(name = "springSessionRepositoryFilter")
  public SessionRepositoryFilter getSpringSessionRepositoryFilter() {
    return new SessionRepositoryFilter<>(mapSessionRepository);
  }
}
