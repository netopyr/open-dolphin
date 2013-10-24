package org.opendolphin.binding

import org.opendolphin.core.client.ClientAttribute

class UnbindClientOtherOfAble {
    final ClientAttribute attribute
    final String targetPropertyName

    UnbindClientOtherOfAble(ClientAttribute attribute, String targetPropertyName) {
        this.attribute = attribute
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName)
        attribute.removePropertyChangeListener('value', listener)
    }
}
