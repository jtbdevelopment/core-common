package com.jtbdevelopment.core.mongo.spring

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter
import org.springframework.core.convert.support.GenericConversionService

/**
 * Date: 1/9/15
 * Time: 6:51 PM
 */
class AbstractCoreMongoConfigurationTest extends GroovyTestCase {
    private static class ConvertibleClass {

    }

    void testCustomConversions() {
        def cc1 = new MongoConverter<String, ConvertibleClass>() {
            @Override
            ConvertibleClass convert(final String source) {
                return null
            }
        }
        def cc2 = new MongoConverter<ConvertibleClass, Integer>() {
            @Override
            Integer convert(final ConvertibleClass source) {
                return null
            }
        }

        MongoConfiguration configuration = new MongoConfiguration([cc1, cc2], null)
        def service = new GenericConversionService()
        configuration.customConversions().registerConvertersIn(service)
        assert service.canConvert(String.class, ConvertibleClass.class)
        assert service.canConvert(ConvertibleClass.class, Integer.class)
    }

    void testGetMappingBasePackage() {
        MongoConfiguration configuration = new MongoConfiguration(null, null)
        assert "com.jtbdevelopment" == configuration.mappingBasePackage
        assert ["com.jtbdevelopment"] == configuration.mappingBasePackages
    }

    void testGetDatabaseName() {
        MongoProperties p = new MongoProperties('X', null, 0, null, null, "JOURNALED")
        MongoConfiguration configuration = new MongoConfiguration([], p)

        assert p.dbName == configuration.databaseName
    }
}
