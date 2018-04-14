package com.jtbdevelopment.spring.jackson

import com.fasterxml.jackson.databind.JsonDeserializer

/**
 * Date: 1/13/15
 * Time: 7:46 PM
 */
abstract class AutoRegistrableJsonDeserializer<T> extends JsonDeserializer<T> {
}
