package com.canoo.dolphin.core

import spock.lang.Specification

class BaseAttributeSpec extends Specification {
    def "simple constructor with null bean and null value"() {
        when:

        def attribute = new MyAttribute("name")

        then:

        attribute.value == null
    }
}

class MyAttribute extends BaseAttribute {
    Object value

    MyAttribute(String propertyName) {
        super(propertyName)
    }
}