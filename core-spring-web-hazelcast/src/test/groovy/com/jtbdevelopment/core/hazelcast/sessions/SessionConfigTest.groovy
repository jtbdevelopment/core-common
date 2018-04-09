package com.jtbdevelopment.core.hazelcast.sessions

import org.springframework.session.MapSessionRepository
import org.springframework.session.web.http.SessionRepositoryFilter

import java.lang.reflect.Field

/**
 * Date: 3/7/15
 * Time: 7:59 PM
 */
class SessionConfigTest extends GroovyTestCase {
    SessionConfig sessionConfig = new SessionConfig()

    void testGetSpringSessionRepositoryFilter() {
        def repository = [:] as MapSessionRepository
        sessionConfig.mapSessionRepository = repository
        SessionRepositoryFilter filter = sessionConfig.getSpringSessionRepositoryFilter()
        Field field = SessionRepositoryFilter.class.getDeclaredField('sessionRepository')
        field.accessible = true
        assert field.get(filter).is(repository)
    }
}
