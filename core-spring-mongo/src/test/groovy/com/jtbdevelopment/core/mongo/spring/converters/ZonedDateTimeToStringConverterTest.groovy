package com.jtbdevelopment.core.mongo.spring.converters

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 11/9/2014
 * Time: 7:54 PM
 */
class ZonedDateTimeToStringConverterTest extends GroovyTestCase {

    void testConvert() {

        def zonedTime = ZonedDateTime.of(2014, 1, 1, 14, 32, 19, 800 * 1000000, ZoneId.of("Europe/Paris"))
        assert new ZonedDateTimeToStringConverter().convert(zonedTime) == "2014-01-01T13:32:19.800Z"
    }
}
