package com.jtbdevelopment.spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.stereotype.Component;

/**
 * Date: 1/13/15 Time: 7:41 PM
 */
@Component
public class ObjectMapperFactory implements FactoryBean<ObjectMapper> {

  private final ObjectMapper objectMapper;

  public ObjectMapperFactory(
      final List<AutoRegistrableJsonSerializer> serializers,
      final List<AutoRegistrableJsonDeserializer> deserializers,
      final List<JacksonModuleCustomization> customizations) {
    objectMapper = new ObjectMapper();
    final SimpleModule module = new SimpleModule("com.jtbdevelopment.spring.jackson.automatic");
    serializers.forEach(s -> module.addSerializer(s.handledType(), s));
    deserializers.forEach(d -> module.addDeserializer(d.handledType(), d));
    customizations.forEach(c -> c.customizeModule(module));
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(module);
  }

  @Override
  public ObjectMapper getObject() throws Exception {
    if (DefaultGroovyMethods.asBoolean(objectMapper)) {
      return objectMapper;
    }

    throw new FactoryBeanNotInitializedException();
  }

  @Override
  public Class<?> getObjectType() {
    return ObjectMapper.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
