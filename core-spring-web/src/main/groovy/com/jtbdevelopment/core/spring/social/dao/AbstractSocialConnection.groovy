package com.jtbdevelopment.core.spring.social.dao

import groovy.transform.CompileStatic
import org.springframework.data.annotation.Version

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 12/16/14
 * Time: 1:04 PM
 */
@CompileStatic
abstract class AbstractSocialConnection<ID extends Serializable> implements SocialConnection<ID> {
    private static final ZoneId GMT = ZoneId.of("GMT")

    @Version
    Integer version
    ZonedDateTime created = ZonedDateTime.now(GMT)

    String userId

    String providerId
    String providerUserId

    String displayName
    String profileUrl
    String imageUrl

    String accessToken
    String secret
    String refreshToken

    Long expireTime
}
