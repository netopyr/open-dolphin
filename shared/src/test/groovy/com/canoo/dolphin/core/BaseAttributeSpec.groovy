package com.canoo.dolphin.core

import java.beans.PropertyChangeListener
import spock.lang.Specification

class BaseAttributeSpec extends Specification {
    def "simple constructor with null bean and null value"() {
        when:

        def attribute = new BaseAttribute("name")

        then:

        attribute.value == null
    }

    def "false constructor calls"() {
        when:

        new BaseAttribute(null)

        then:

        thrown(AssertionError)
    }

    void "listener is notified when an attribute value changes"() {
        given:

        def attribute = new BaseAttribute('name')
        attribute.value = ""
        def changeListener = Mock(PropertyChangeListener)

        when:

        attribute.addPropertyChangeListener("value", changeListener)
        attribute.value = 'newValue'

        then:

        1 * changeListener.propertyChange(_)
    }
}