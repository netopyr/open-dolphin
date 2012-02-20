package com.canoo.dolphin.core.server

import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Unroll

import com.canoo.dolphin.core.BaseAttribute

class PresentationModelSpec extends Specification {

    @Shared
    def testBean = new TestBean(name: "1")
    @Shared
    def secondTestBean = new TestBean(name: "2")
    @Shared
    def otherTestBean = new OtherTestBean(name: "3")

    def "simple constructor with immutable list of one Attribute"() {
        given:
            def attribute = new BaseAttribute(TestBean, "name")
            def list = [attribute]
        when:
            def pm = new ServerPresentationModel(list)
            list.clear()
        then:
            pm.attributes.size() == 1
    }

    def "false constructor calls"() {
        when:
            new ServerPresentationModel(list)
        then:
            thrown(AssertionError)
        where:
            list << [ null, [] ]
    }

    @Unroll
    def "when the bean of a PM changes, all attributes change accordingly"() {
        given:
            def testAttr = new BaseAttribute(TestBean, "name")
            testAttr.bean = testBean
            def otherTestAttr = new BaseAttribute(OtherTestBean, "name")
            otherTestAttr.bean = otherTestBean
            def nullTestAttr = new BaseAttribute(TestBean, 'name') // has no bean
            def pm = new ServerPresentationModel([testAttr, otherTestAttr, nullTestAttr])
        when:
            pm.changeBean(oldBean, newBean)
        then:
            testAttr.bean      == expectedTestBean
            otherTestAttr.bean == expectedOtherTestBean
            nullTestAttr.bean  == expectedNullTestBean
        where:
        oldBean       | newBean        | expectedTestBean | expectedOtherTestBean | expectedNullTestBean
        testBean      | secondTestBean | secondTestBean   | otherTestBean         | null // happy path
        testBean      | null           | null             | otherTestBean         | null // setting to null, happy path
        otherTestBean | null           | testBean         | null                  | null // same on other instance
        null          | null           | testBean         | otherTestBean         | null // no change
        null          | secondTestBean | testBean         | otherTestBean         | secondTestBean // change by Type where null
    }



}

class OtherTestBean {
    String name
    String toString() {name}
}

