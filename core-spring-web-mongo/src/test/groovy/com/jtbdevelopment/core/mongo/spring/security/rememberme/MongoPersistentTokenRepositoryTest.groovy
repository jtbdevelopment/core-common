package com.jtbdevelopment.core.mongo.spring.security.rememberme

import com.jtbdevelopment.core.spring.security.rememberme.AbstractPersistentTokenRepositoryTest

/**
 * Date: 1/2/15
 * Time: 4:09 PM
 */
class MongoPersistentTokenRepositoryTest extends AbstractPersistentTokenRepositoryTest {
    @Override
    protected void setUp() throws Exception {
        this.repository = new MongoPersistentTokenRepository()
    }
}
