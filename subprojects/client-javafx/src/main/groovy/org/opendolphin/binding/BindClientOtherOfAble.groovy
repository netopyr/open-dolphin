package org.opendolphin.binding

import org.opendolphin.core.client.ClientAttribute

class BindClientOtherOfAble {
    final ClientAttribute attribute
    final String targetPropertyName

    BindClientOtherOfAble(ClientAttribute attribute, String targetPropertyName) {
        this.attribute = attribute
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target, Closure converter = null) {
        of target, converter == null ? null : new ConverterAdapter(converter)
    }
    void of(Object target, Converter converter) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName, converter)
        attribute.addPropertyChangeListener('value', listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }
}
