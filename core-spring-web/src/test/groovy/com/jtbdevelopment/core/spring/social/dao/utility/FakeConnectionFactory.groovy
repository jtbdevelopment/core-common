package com.jtbdevelopment.core.spring.social.dao.utility

import org.springframework.social.connect.ConnectionFactory

/**
 * Date: 1/4/2015
 * Time: 7:53 AM
 */
abstract class FakeConnectionFactory<A> extends ConnectionFactory<A> {

    FakeConnectionFactory(final String providerId) {
        super(providerId, null, null)
    }
}