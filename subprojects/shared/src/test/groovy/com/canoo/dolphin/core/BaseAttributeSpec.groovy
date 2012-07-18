package com.canoo.dolphin.core

import spock.lang.Specification
import java.beans.PropertyChangeListener

class BaseAttributeSpec extends Specification {
    def "simple constructor with null bean and null value"() {
        when:

        def attribute = new MyAttribute("name")

        then:

        attribute.initialValue == null
        attribute.value == null
    }

    def "check isDirty triggers when value changes (initialValue == null)"() {
        given:

        def attribute = new MyAttribute("name")
        def changeListener = Mock(PropertyChangeListener)

        when:

        attribute.addPropertyChangeListener(Attribute.DIRTY_PROPERTY, changeListener)
        attribute.value = 'foo'

        then:

        1 * changeListener.propertyChange(_)
        !attribute.initialValue
        attribute.value == 'foo'
        attribute.dirty

        when:

        attribute.value = 'foo'

        then:

        0 * changeListener.propertyChange(_)
        !attribute.initialValue
        attribute.value == 'foo'
        attribute.dirty

        when:

        attribute.value = null

        then:

        1 * changeListener.propertyChange(_)
        !attribute.initialValue
        !attribute.value
        !attribute.dirty
    }

    def "check isDirty triggers when value changes (initialValue == bar)"() {
        given:

        def attribute = new MyAttribute("name", 'bar')
        def changeListener = Mock(PropertyChangeListener)

        when:

        attribute.addPropertyChangeListener(Attribute.DIRTY_PROPERTY, changeListener)
        attribute.value = 'foo'

        then:

        1 * changeListener.propertyChange(_)
        attribute.initialValue == 'bar'
        attribute.value == 'foo'
        attribute.dirty

        when:

        attribute.value = 'foo'

        then:

        0 * changeListener.propertyChange(_)
        attribute.initialValue == 'bar'
        attribute.value == 'foo'
        attribute.dirty

        when:

        attribute.value = null

        then:

        0 * changeListener.propertyChange(_)
        attribute.initialValue == 'bar'
        !attribute.value
        attribute.dirty

        when:

        attribute.value = 'bar'

        then:

        1 * changeListener.propertyChange(_)
        attribute.initialValue == 'bar'
        attribute.value == 'bar'
        !attribute.dirty
    }
}

class MyAttribute extends BaseAttribute {
    MyAttribute(String propertyName) {
        super(propertyName)
    }

    MyAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }
}