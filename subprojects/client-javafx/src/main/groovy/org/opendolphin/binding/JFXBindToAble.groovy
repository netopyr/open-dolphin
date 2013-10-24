package org.opendolphin.binding

class JFXBindToAble {
    final javafx.scene.Node source
    final String sourcePropertyName

    JFXBindToAble(javafx.scene.Node source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    JFXBindOtherOfAble to(String targetPropertyName) {
        new JFXBindOtherOfAble(source, sourcePropertyName, targetPropertyName)
    }
}
