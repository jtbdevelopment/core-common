package com.jtbdevelopment.spring.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Date: 2/8/15 Time: 3:23 PM
 */
public interface JacksonModuleCustomization {

  void customizeModule(final SimpleModule module);
}
