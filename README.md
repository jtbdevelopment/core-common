[![Build Status](https://travis-ci.org/jtbdevelopment/core-common.svg?branch=master)](https://travis-ci.org/jtbdevelopment/core-common)

core-common
===========

Common Utilities

common-spring
=============
- Registers a property handler placeholder for the many @Values used throughout
- Also defines a base list handling cache usable in spring data caching annotations to cache a list of results to individual items as well or to only lookup missing ids

common-spring-web
=================
Common Spring Utilities Needed When using spring security and/or social

- Password Encryption using configurabl BCrypt which can be autowired
- Strong Text Encryption (using standard JRE, no US Export) with properties
- Registers text encyrption for Abstract Social but can be used anywhere strong encryption is required
- Abstract Spring Data Implementations of spring-security PersistentRememberMeTokenRepository
- Abstract Spring Data Implementations of spring-social UsersConnectionRepository and ConnectionRepository

common-spring-mongo
===================
Common setup and easy registration of converters for Spring mongo as well as converters for JDK1.8 new date time constructs (not available when originally released


common-spring-web-mongo
=======================
spring-data-mongo implementations of

- spring-security PersistentRememberMeTokenRepository
- spring-social UsersConnectionRepository
- generic configuration module for kicking off mongo spring-data

common-spring-hazelcast
=======================
Common setup for spring hazelcast, with AWS hooks - see HazelcastConfigurer for customizing
