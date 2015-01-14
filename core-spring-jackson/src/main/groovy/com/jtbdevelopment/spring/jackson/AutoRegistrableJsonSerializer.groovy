package com.jtbdevelopment.spring.jackson

import com.fasterxml.jackson.databind.JsonSerializer

/**
 * Date: 1/13/15
 * Time: 7:46 PM
 */
abstract class AutoRegistrableJsonSerializer<T> extends JsonSerializer<T> {
    abstract Class<T> registerForClass()
}
