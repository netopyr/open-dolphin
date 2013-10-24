package org.opendolphin.binding

import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel

class JFXUnbindOfAble {
    private String sourcePropertyName

    JFXUnbindOfAble(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName
    }

    JFXUnbindFromAble of(javafx.scene.Node source) {
        new JFXUnbindFromAble(source, sourcePropertyName)
    }

    UnbindFromAble of(PresentationModel source) {
        return Binder.unbind(sourcePropertyName).of(source)
    }

    UnbindClientFromAble of(ClientPresentationModel source) {
        new UnbindClientFromAble((ClientAttribute) source.findAttributeByPropertyName(sourcePropertyName))
    }

    UnbindPojoFromAble of(Object source) {
        return Binder.unbind(sourcePropertyName).of(source)
    }
}
