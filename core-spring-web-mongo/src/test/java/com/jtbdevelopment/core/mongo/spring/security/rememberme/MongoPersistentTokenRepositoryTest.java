package com.jtbdevelopment.core.mongo.spring.security.rememberme;

import com.jtbdevelopment.core.spring.security.rememberme.AbstractPersistentTokenRepositoryTest;
import org.junit.Before;

/**
 * Date: 1/2/15 Time: 4:09 PM
 */
public class MongoPersistentTokenRepositoryTest extends AbstractPersistentTokenRepositoryTest {

  @Before
  public void setUp() throws Exception {
    super.setup();
    this.repository = new MongoPersistentTokenRepository(tokenRepository);
  }

}
