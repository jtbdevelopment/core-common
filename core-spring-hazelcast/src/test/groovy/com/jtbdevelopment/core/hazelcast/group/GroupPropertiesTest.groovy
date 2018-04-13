package com.jtbdevelopment.core.hazelcast.group

/**
 * Date: 11/25/16
 * Time: 7:02 AM
 */
class GroupPropertiesTest extends GroovyTestCase {
    void testWhenHazelcastGroupSet() {
        GroupProperties p = new GroupProperties("M", "FB", "HZ")
        assert 'HZ' == p.groupSetting
    }

    void testWhenHazelcastGroupNotSetAndMongoIs() {
        GroupProperties p = new GroupProperties("M", "FB", null)
        assert 'M' == p.groupSetting
    }

    void testWhenHazelcastGroupNotSetAndMongoIsNotAndFBIS() {
        GroupProperties p = new GroupProperties(null, "FB", "")
        assert 'FB' == p.groupSetting
    }

    void testWhenNothingIsSet() {
        GroupProperties p = new GroupProperties(null, "", "")
        assert '' == p.groupSetting
    }
}
