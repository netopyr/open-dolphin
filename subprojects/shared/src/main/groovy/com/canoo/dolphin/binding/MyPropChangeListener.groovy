package com.canoo.dolphin.binding

import groovy.transform.Canonical

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

@Canonical
class MyPropChangeListener implements PropertyChangeListener{
    def target
    def targetPropName
    Closure converter

    void propertyChange(PropertyChangeEvent evt) {
        target[targetPropName] = convert(evt.newValue)
    }

    Object convert(Object value) {
        converter != null ? converter(value) : value
    }
}
