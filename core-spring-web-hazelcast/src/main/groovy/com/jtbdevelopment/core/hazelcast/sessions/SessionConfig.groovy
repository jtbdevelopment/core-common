package com.jtbdevelopment.core.hazelcast.sessions

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.MapSessionRepository
import org.springframework.session.web.http.SessionRepositoryFilter

/**
 * Date: 3/6/15
 * Time: 6:41 PM
 */
@Configuration
@CompileStatic
class SessionConfig {
    @Autowired
    MapSessionRepository mapSessionRepository

    @Bean(name = 'springSessionRepositoryFilter')
    SessionRepositoryFilter getSpringSessionRepositoryFilter() {
        return new SessionRepositoryFilter(mapSessionRepository)
    }
}
