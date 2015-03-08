package com.jtbdevelopment.core.mongo.spring.converters

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Date: 11/9/2014
 * Time: 7:03 PM
 *
 *   TODO - these may not be necessary with latest spring data
 */
@Component
@CompileStatic
class ZonedDateTimeToStringConverter implements MongoConverter<ZonedDateTime, String> {
    @Override
    String convert(final ZonedDateTime source) {
        return source.format(DateTimeFormatter.ISO_INSTANT)
    }
}
