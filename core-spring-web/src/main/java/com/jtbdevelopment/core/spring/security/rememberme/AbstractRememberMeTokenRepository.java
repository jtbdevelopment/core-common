package com.jtbdevelopment.core.spring.security.rememberme;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Date: 12/30/2014 Time: 3:42 PM
 *
 * Currently choosing to not cache The spring libraries themselves do some in-memory caching and the
 * spring session layer shares sessions across cluster
 */
@NoRepositoryBean
public interface AbstractRememberMeTokenRepository<
    ID extends Serializable, T extends AbstractRememberMeToken<ID>
    > extends CrudRepository<T, ID> {

  T findBySeries(final String series);

  List<T> findByUsername(final String username);
}
