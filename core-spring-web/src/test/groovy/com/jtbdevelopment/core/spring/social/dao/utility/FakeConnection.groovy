package com.jtbdevelopment.core.spring.social.dao.utility

import org.springframework.social.connect.ConnectionData
import org.springframework.social.connect.support.AbstractConnection

/**
 * Date: 1/4/2015
 * Time: 7:50 AM
 */
abstract class FakeConnection<A> extends AbstractConnection<A> {
    final Long expireTime
    final String accessToken
    final String refreshToken
    final String secret
    final String providerUserId
    final String providerId

    FakeConnection(final ConnectionData data) {
        super(data, null)
        this.expireTime = data.expireTime
        this.accessToken = data.accessToken
        this.refreshToken = data.refreshToken
        this.secret = data.secret
        this.providerUserId = data.providerUserId
        this.providerId = data.providerId
    }

    @Override
    ConnectionData createData() {
        return new ConnectionData(providerId, providerUserId, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime)
    }
}
