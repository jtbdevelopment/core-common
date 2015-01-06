package com.jtbdevelopment.core.spring.social.dao.utility

import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionData

/**
 * Date: 1/4/2015
 * Time: 7:53 AM
 */
class FakeTwitterConnectionFactory extends FakeConnectionFactory<FakeTwitterApi> {
    FakeTwitterConnectionFactory() {
        super(FakeTwitterApi.TWITTER)
    }

    @Override
    Connection<FakeTwitterApi> createConnection(final ConnectionData data) {
        return new FakeTwitterConnection(data)
    }
}