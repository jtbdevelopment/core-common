package com.jtbdevelopment.core.mongo.spring.converters

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 11/9/2014
 * Time: 7:37 PM
 */
class StringToZonedDateTimeConverterTest extends GroovyTestCase {

    void testConvert() {
        assert new StringToZonedDateTimeConverter().convert("2014-11-10T00:35:15.809Z") == ZonedDateTime.of(2014, 11, 10, 00, 35, 15, 809 * 1000000, ZoneId.of("GMT"))
    }
}
