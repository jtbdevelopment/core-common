package com.jtbdevelopment.core.hazelcast.group

/**
 * Date: 11/25/16
 * Time: 7:02 AM
 */
class GroupPropertiesTest extends GroovyTestCase {
    void testWhenHazelcastGroupSet() {
        GroupProperties p = new GroupProperties(groupSetting: 'HZ', facebookClientID: 'FB', mongoDbName: 'M')
        p.setup()
        assert 'HZ' == p.groupSetting
    }

    void testWhenHazelcastGroupNotSetAndMongoIs() {
        GroupProperties p = new GroupProperties(groupSetting: '', facebookClientID: 'FB', mongoDbName: 'M')
        p.setup()
        assert 'M' == p.groupSetting
    }

    void testWhenHazelcastGroupNotSetAndMongoIsNotAndFBIS() {
        GroupProperties p = new GroupProperties(groupSetting: '', facebookClientID: 'FB', mongoDbName: null)
        p.setup()
        assert 'FB' == p.groupSetting
    }

    void testWhenNothingIsSet() {
        GroupProperties p = new GroupProperties(groupSetting: '', facebookClientID: null, mongoDbName: null)
        p.setup()
        assert '' == p.groupSetting
    }
}
