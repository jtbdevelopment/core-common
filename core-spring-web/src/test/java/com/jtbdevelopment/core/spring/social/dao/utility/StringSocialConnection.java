package com.jtbdevelopment.core.spring.social.dao.utility;

import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnection;

/**
 * Date: 4/16/18 Time: 8:42 PM
 */
public class StringSocialConnection extends AbstractSocialConnection<String> {

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
