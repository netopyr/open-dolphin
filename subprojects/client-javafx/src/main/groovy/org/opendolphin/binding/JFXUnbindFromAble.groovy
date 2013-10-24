package org.opendolphin.binding

class JFXUnbindFromAble {
    final javafx.scene.Node source
    final String sourcePropertyName

    JFXUnbindFromAble(javafx.scene.Node source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    JFXUnbindOtherOfAble from(String targetPropertyName) {
        new JFXUnbindOtherOfAble(source, sourcePropertyName, targetPropertyName)
    }
}
