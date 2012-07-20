package com.canoo.dolphin.core.client

import javafx.beans.value.ChangeListener
import spock.lang.Specification

class ClientAttributeWrapperSpec extends Specification {
    void "ChangeListener is notified when an attribute value changes"() {
        given:

        def attribute = new ClientAttribute('name')
        def wrapper = new ClientAttributeWrapper(attribute)
        attribute.value = ""
        def changeListener = Mock(ChangeListener)

        when:

        wrapper.addListener(changeListener)
        attribute.value = 'newValue'

        then:

        1 * changeListener.changed(_, _, _)
        attribute.value == 'newValue'
        wrapper.get() == 'newValue'

        when:

        wrapper.set('latestValue')

        then:

        1 * changeListener.changed(_, _, _)
        attribute.value == 'latestValue'
        wrapper.get() == 'latestValue'
    }
}
