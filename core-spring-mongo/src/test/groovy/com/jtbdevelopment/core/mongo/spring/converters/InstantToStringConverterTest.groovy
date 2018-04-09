package com.jtbdevelopment.core.mongo.spring.converters

import java.time.ZoneId
import java.time.ZonedDateTime

class InstantToStringConverterTest extends GroovyTestCase {
    void testConvert() {
        def zonedTime = ZonedDateTime.of(2014, 1, 1, 14, 32, 19, 800 * 1000000, ZoneId.of("Europe/Paris"))
        assert new InstantToStringConverter().convert(zonedTime.toInstant()) == "2014-01-01T13:32:19.800Z"
    }
}
