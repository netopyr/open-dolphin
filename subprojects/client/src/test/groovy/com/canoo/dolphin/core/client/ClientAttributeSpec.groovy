package com.canoo.dolphin.core.client

import spock.lang.Specification

import java.beans.PropertyChangeListener

class ClientAttributeSpec extends Specification {
    void "PropertyChangeListener is notified when an attribute value changes"() {
        given:

        def attribute = new ClientAttribute('name')
        attribute.value = ""
        def changeListener = Mock(PropertyChangeListener)

        when:

        attribute.addPropertyChangeListener("value", changeListener)
        attribute.value = 'newValue'

        then:

        1 * changeListener.propertyChange(_)
        attribute.value == 'newValue'
    }
}
