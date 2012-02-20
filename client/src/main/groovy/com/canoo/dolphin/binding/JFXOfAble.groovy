package com.canoo.dolphin.binding

import com.canoo.dolphin.core.BasePresentationModel

@Immutable
class JFXOfAble {
    String propName

    JFXToAble of(javafx.scene.Node source) {
        new JFXToAble(source, propName)
    }

    ToAble of(BasePresentationModel source) {
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

class JFXOtherOfAble {
    final javafx.scene.Node source
    final String sourcePropName
    final String targetPropName

    JFXOtherOfAble(javafx.scene.Node source, String sourcePropName, String targetPropName) {
        this.source = source
        this.sourcePropName = sourcePropName
        this.targetPropName = targetPropName
    }

    void of(javafx.scene.Node target) {
        target."${targetPropName}Property"().bind(source."${sourcePropName}Property"())
    }
}
