package com.jtbdevelopment.core.spring.security.crypto.encrypt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 12/16/14
 * Time: 10:49 PM
 */
@Component
class TextEncryptionProperties {
    private static final Logger logger = LoggerFactory.getLogger(TextEncryptionProperties.class)
    @Value('${textEncryption.password:NOTSET}')
    String password

    @Value('${textEncryption.salt:NOTSET}')
    String salt;

    @PostConstruct
    public void testDefaults() {
        if (password == 'NOTSET' || salt == 'NOTSET') {
            logger.warn('-----------------------------------------------------------------------------')
            logger.warn('-----------------------------------------------------------------------------')
            logger.warn('-----------------------------------------------------------------------------')
            logger.warn('textEncryption.password AND/OR textEncryption.salt is using default values!!!')
            logger.warn('-----------------------------------------------------------------------------')
            logger.warn('-----------------------------------------------------------------------------')
            logger.warn('-----------------------------------------------------------------------------')
        }
    }
}
