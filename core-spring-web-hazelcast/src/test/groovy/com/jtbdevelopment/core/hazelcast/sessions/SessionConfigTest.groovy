package com.jtbdevelopment.core.hazelcast.sessions

import org.springframework.session.MapSessionRepository
import org.springframework.session.web.http.SessionRepositoryFilter

import java.lang.reflect.Field

import static org.mockito.Mockito.mock

/**
 * Date: 3/7/15
 * Time: 7:59 PM
 */
class SessionConfigTest extends GroovyTestCase {
    MapSessionRepository sessionRepository = mock(MapSessionRepository.class)
    SessionConfig sessionConfig = new SessionConfig(sessionRepository)

    void testGetSpringSessionRepositoryFilter() {
        SessionRepositoryFilter filter = sessionConfig.getSpringSessionRepositoryFilter()
        Field field = SessionRepositoryFilter.class.getDeclaredField('sessionRepository')
        field.accessible = true
        assert field.get(filter).is(sessionRepository)
    }
}
