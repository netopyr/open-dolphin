package org.opendolphin.binding

class JFXBindToAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final Converter converter

    JFXBindToAble(javafx.scene.Node source, String sourcePropertyName, Converter converter = null) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.converter = converter
    }

    JFXBindOtherOfAble to(String targetPropertyName) {
        new JFXBindOtherOfAble(source, sourcePropertyName, targetPropertyName, converter)
    }

    JFXBindToAble using(Closure converter) {
        using(new ConverterAdapter(converter))
    }

    JFXBindToAble using(Converter converter) {
        return new JFXBindToAble(source, sourcePropertyName, converter)
    }

}
