core-common
===========

Common Utilities

common-spring
=============
- Registers a property handler placeholder for the many @Values used throughout

common-spring-web
=================
Common Spring Utilities Needed When using spring security and/or social

- Password Encryption using configurabl BCrypt which can be autowired
- Strong Text Encryption (using standard JRE, no US Export) with properties
- Registers text encyrption for Abstract Social but can be used anywhere strong encryption is required
- Abstract Spring Data Implementations of spring-security PersistentRememberMeTokenRepository
- Abstract Spring Data Implementations of spring-social UsersConnectionRepository and ConnectionRepository

common-spring-web-mongo
=======================
spring-data-mongo implementations of

- spring-security PersistentRememberMeTokenRepository
- spring-social UsersConnectionRepository
- generic configuration module for kicking off mongo spring-data

