package org.opendolphin.binding

import org.opendolphin.core.client.ClientAttribute

class BindClientToAble {
    final ClientAttribute attribute

    BindClientToAble(ClientAttribute attribute) {
        this.attribute = attribute
    }

    BindClientOtherOfAble to(String targetPropertyName) {
        new BindClientOtherOfAble(attribute, targetPropertyName)
    }
}
