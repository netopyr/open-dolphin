package org.opendolphin.binding

import groovy.transform.Canonical
import org.opendolphin.core.Attribute

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

@Canonical
class JFXBinderPropertyChangeListener implements PropertyChangeListener {
    Attribute attribute
    Object target
    String targetPropertyName
    Converter converter

    void update() {
        target[targetPropertyName] = convert(attribute.value)
    }

    void propertyChange(PropertyChangeEvent evt) {
        update()
    }

    Object convert(Object value) {
        converter != null ? converter.convert(value) : value
    }
    // we have equals(o) and hashCode() from @Canonical
}
