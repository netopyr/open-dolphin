package com.canoo.dolphin.binding

import groovy.transform.Canonical
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent

@Canonical
class MyPropChangeListener implements PropertyChangeListener{
    def target
    def targetPropName

    void propertyChange(PropertyChangeEvent evt) {
        target[targetPropName] = evt.newValue
    }
}
