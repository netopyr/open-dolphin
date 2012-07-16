package com.canoo.dolphin.core.client

import spock.lang.Specification
import java.beans.PropertyChangeListener
import javafx.beans.value.ChangeListener
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector


class ClientAttributeSpec extends Specification {
    void "PropertyChangeListener is notified when an attribute value changes"() {
        given:

        new ClientModelStore(InMemoryClientConnector.instance)
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

    void "ChangeListener is notified when an attribute value changes"() {
        given:

        new ClientModelStore(InMemoryClientConnector.instance)
        def attribute = new ClientAttribute('name')
        attribute.value = ""
        def changeListener = Mock(ChangeListener)

        when:


        attribute.valueProperty().addListener(changeListener)
        attribute.value = 'newValue'

        then:

        1 * changeListener.changed(_, _, _)
        attribute.value == 'newValue'
    }
}
