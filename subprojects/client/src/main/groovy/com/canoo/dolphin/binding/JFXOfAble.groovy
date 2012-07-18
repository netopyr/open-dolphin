package com.canoo.dolphin.binding

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import javafx.beans.value.ChangeListener

import java.beans.PropertyChangeListener

@Immutable
class JFXOfAble {
    String propName

    JFXToAble of(javafx.scene.Node source) {
        new JFXToAble(source, propName)
    }

    ToAble of(PresentationModel source) {
        return Binder.bind(propName).of(source)
    }

    ClientToAble of(ClientPresentationModel source) {
        new ClientToAble(source.findAttributeByPropertyName(propName))
    }

    PojoToAble of(Object source) {
        return Binder.bind(propName).of(source)
    }
}

class JFXToAble {
    final javafx.scene.Node source
    final String sourcePropName

    JFXToAble(javafx.scene.Node source, String sourcePropName) {
        this.source = source
        this.sourcePropName = sourcePropName
    }

    JFXOtherOfAble to(String targetPropertyName) {
        new JFXOtherOfAble(source, sourcePropName, targetPropertyName)
    }
}

class ClientToAble {
    final ClientAttribute attribute

    ClientToAble(ClientAttribute attribute) {
        this.attribute = attribute
    }

    ClientOtherOfAble to(String targetPropertyName) {
        new ClientOtherOfAble(attribute, targetPropertyName)
    }
}

class JFXOtherOfAble {
    final javafx.scene.Node source
    final String sourcePropName
    final String targetPropName

    JFXOtherOfAble(javafx.scene.Node source, String sourcePropName, String targetPropName) {
        this.source = source
        this.sourcePropName = sourcePropName
        this.targetPropName = targetPropName
    }

    void of(target, Closure convert = null) {  // todo dk: remove the duplication
        def update = {
            target[targetPropName] = (convert != null) ? convert(source[sourcePropName]) : source[sourcePropName]
        }
        source."${sourcePropName}Property"().addListener({ a, b, c -> update() } as ChangeListener)
        update() // set the initial value after the binding and trigger the first notification
    }

    void of(ClientPresentationModel presentationModel, Closure convert = null) {  // todo dk: remove the duplication
        def update = {
            presentationModel[targetPropName].value = (convert != null) ? convert(source[sourcePropName]) : source[sourcePropName]
        }
        source."${sourcePropName}Property"().addListener({ a, b, c -> update() } as ChangeListener)
        update() // set the initial value after the binding and trigger the first notification
    }
}

class ClientOtherOfAble {
    final ClientAttribute attribute
    final String targetPropName

    ClientOtherOfAble(ClientAttribute attribute, String targetPropName) {
        this.attribute = attribute
        this.targetPropName = targetPropName
    }

    void of(target, Closure convert = null) { // todo dk: remove the duplication
        def update = {
            target[targetPropName] = (convert != null) ? convert(attribute.value) : attribute.value
        }
        attribute.addPropertyChangeListener({ update() } as PropertyChangeListener)
        update() // set the initial value after the binding and trigger the first notification
    }
}
