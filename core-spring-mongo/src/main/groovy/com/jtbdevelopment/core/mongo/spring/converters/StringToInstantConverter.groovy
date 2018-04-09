package com.jtbdevelopment.core.mongo.spring.converters

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import java.time.Instant

/**
 * Date: 11/9/2014
 * Time: 7:08 PM
 */
@Component
@CompileStatic
class StringToInstantConverter implements MongoConverter<String, Instant> {
    @Override
    Instant convert(final String source) {
        return Instant.parse(source)
    }
}
