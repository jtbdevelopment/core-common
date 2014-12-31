package com.jtbdevelopment.core.spring.security.rememberme

import groovy.transform.CompileStatic
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

/**
 * Date: 12/30/2014
 * Time: 3:42 PM
 */
@CompileStatic
@NoRepositoryBean
interface AbstractRememberMeTokenRepository<ID extends Serializable, T extends AbstractRememberMeToken<ID>> extends CrudRepository<T, ID> {
    T findBySeries(final String series);

    List<T> findByUsername(final String username);
}

