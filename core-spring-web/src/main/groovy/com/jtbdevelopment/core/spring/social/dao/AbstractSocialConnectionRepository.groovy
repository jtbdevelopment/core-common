package com.jtbdevelopment.core.spring.social.dao

import groovy.transform.CompileStatic
import org.springframework.data.domain.Sort
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Date: 12/30/2014
 * Time: 3:42 PM
 */
@NoRepositoryBean
@CompileStatic
interface AbstractSocialConnectionRepository<ID extends Serializable, IMPL extends AbstractSocialConnection<ID>> extends PagingAndSortingRepository<IMPL, ID> {
    List<IMPL> findByUserId(final String userId, final Sort sort);

    List<IMPL> findByUserIdAndProviderId(final String userId, final String providerId, final Sort sort);

    IMPL findByUserIdAndProviderIdAndProviderUserId(
            final String userId, final String providerId, final String providerUserId);

    List<IMPL> findByUserIdAndProviderIdAndProviderUserIdIn(
            final String userId, final String providerId, final Collection<String> providerUserIds, final Sort sort);

    List<IMPL> findByProviderIdAndProviderUserId(final String providerId, final String providerUserId);

    List<IMPL> findByProviderIdAndProviderUserIdIn(final String providerId, final Collection<String> providerUserIds);

    Long deleteByUserIdAndProviderId(final String userId, final String providerId);

    Long deleteByUserIdAndProviderIdAndProviderUserId(
            final String userId, final String providerId, final String providerUserId);
}
