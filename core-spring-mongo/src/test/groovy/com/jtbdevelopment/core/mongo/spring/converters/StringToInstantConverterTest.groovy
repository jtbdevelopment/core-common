package com.jtbdevelopment.core.mongo.spring.converters

import java.time.ZoneId
import java.time.ZonedDateTime

class StringToInstantConverterTest extends GroovyTestCase {
    void testConvert() {
        def converter = new StringToInstantConverter()
        def expected = ZonedDateTime.of(2014, 11, 10, 00, 35, 15, 809 * 1000000, ZoneId.of("GMT")).toInstant()
        assert converter.convert("2014-11-10T00:35:15.809Z") == expected
    }
}
