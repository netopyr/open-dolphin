package org.opendolphin.binding

import org.opendolphin.core.client.ClientAttribute

class UnbindClientFromAble {
    final ClientAttribute attribute

    UnbindClientFromAble(ClientAttribute attribute) {
        this.attribute = attribute
    }

    UnbindClientOtherOfAble from(String targetPropertyName) {
        new UnbindClientOtherOfAble(attribute, targetPropertyName)
    }
}
