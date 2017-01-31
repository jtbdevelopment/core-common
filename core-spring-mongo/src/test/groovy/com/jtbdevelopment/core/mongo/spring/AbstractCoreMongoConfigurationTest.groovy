package com.jtbdevelopment.core.mongo.spring

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter
import org.springframework.core.convert.support.GenericConversionService

/**
 * Date: 1/9/15
 * Time: 6:51 PM
 */
class AbstractCoreMongoConfigurationTest extends GroovyTestCase {
    MongoConfiguration configuration = new MongoConfiguration()

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

        configuration.mongoConverters = [cc1, cc2]
        def service = new GenericConversionService()
        configuration.customConversions().registerConvertersIn(service)
        assert service.canConvert(String.class, ConvertibleClass.class)
        assert service.canConvert(ConvertibleClass.class, Integer.class)
    }

    void testGetMappingBasePackage() {
        assert configuration.mappingBasePackage == "com.jtbdevelopment"
    }

    void testGetDatabaseName() {
        MongoProperties p = new MongoProperties()
        p.dbName = 'X'
        configuration.mongoProperties = p

        assert p.dbName == configuration.databaseName
    }
}
