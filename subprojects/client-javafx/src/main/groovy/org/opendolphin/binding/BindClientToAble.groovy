package org.opendolphin.binding

import org.opendolphin.core.client.ClientAttribute

class BindClientToAble {
    final ClientAttribute attribute
    final Converter converter

    BindClientToAble(ClientAttribute attribute, Converter converter = null) {
        this.attribute = attribute
        this.converter = converter
    }

    BindClientOtherOfAble to(String targetPropertyName) {
        new BindClientOtherOfAble(attribute, targetPropertyName, converter)
    }

    BindClientToAble using(Closure converter) {
        using(new ConverterAdapter(converter))
    }

    BindClientToAble using(Converter converter) {
        return new BindClientToAble(attribute, converter)
    }

}
