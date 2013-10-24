package org.opendolphin.binding

import groovy.transform.Canonical
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import org.opendolphin.core.PresentationModel

@Canonical
class JFXBinderChangeListener implements ChangeListener {
    javafx.scene.Node source
    String sourcePropertyName
    Object target
    String targetPropertyName
    Converter converter

    void update() {
        if (target instanceof PresentationModel) {
            target[targetPropertyName].value = convert(source[sourcePropertyName])
        } else {
            target[targetPropertyName] = convert(source[sourcePropertyName])
        }
    }

    void changed(ObservableValue oe, Object oldValue, Object newValue) {
        update()
    }

    Object convert(Object value) {
        converter != null ? converter.convert(value) : value
    }

    // we have equals(o) and hashCode() from @Canonical

}
