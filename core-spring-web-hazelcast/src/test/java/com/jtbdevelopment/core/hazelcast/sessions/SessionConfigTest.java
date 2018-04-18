package com.jtbdevelopment.core.hazelcast.sessions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 3/7/15 Time: 7:59 PM
 */
public class SessionConfigTest {

  private MapSessionRepository sessionRepository = mock(MapSessionRepository.class);
  private SessionConfig sessionConfig = new SessionConfig(sessionRepository);

  @Test
  public void testGetSpringSessionRepositoryFilter() {
    SessionRepositoryFilter filter = sessionConfig.getSpringSessionRepositoryFilter();
    assertEquals(sessionRepository, ReflectionTestUtils.getField(filter, "sessionRepository"));
  }
}
