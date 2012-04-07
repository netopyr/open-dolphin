package com.canoo.dolphin.binding

import com.canoo.dolphin.core.BasePresentationModel
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

@Immutable
class JFXOfAble {
    String propName

    JFXToAble of(javafx.scene.Node source) {
        new JFXToAble(source, propName)
    }

    ToAble of(BasePresentationModel source) {
        return Binder.bind(propName).of(source)
    }

    ClientToAble of(ClientPresentationModel source) {
        new ClientToAble(source[propName])
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

    void of(target) { // cannot use Node type here since e.g. stage is not a node
        target."${targetPropName}Property"().bind(source."${sourcePropName}Property"())
    }
}

class ClientOtherOfAble {
    final ClientAttribute attribute
    final String targetPropName

    ClientOtherOfAble(ClientAttribute attribute, String targetPropName) {
        this.attribute = attribute
        this.targetPropName = targetPropName
    }

    void of(target, Closure convert = null) { // cannot use Node type here since e.g. stage is not a node
        def update = {
            target[targetPropName] = (convert != null) ? convert(attribute.value) : attribute.value
        }
        attribute.valueProperty().addListener( { a,b,c -> update() } as ChangeListener  )
        update () // set the initial value after the binding and trigger the first notification
    }
}
