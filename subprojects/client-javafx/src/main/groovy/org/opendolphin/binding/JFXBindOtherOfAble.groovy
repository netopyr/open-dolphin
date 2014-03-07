package org.opendolphin.binding

class JFXBindOtherOfAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final String targetPropertyName
    final Converter converter

    JFXBindOtherOfAble(javafx.scene.Node source, String sourcePropertyName, String targetPropertyName, Converter converter) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
        this.converter = converter
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Closure converter) {
        of target, new ConverterAdapter(converter)
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Converter converter) {
        def listener = new JFXBinderChangeListener(source, sourcePropertyName, target, targetPropertyName, converter)
        // blindly add the listener as Property does not expose a method to query existing listeners
        // javafx 2.2b17
        source."${sourcePropertyName}Property"().addListener(listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

    void of(Object target) {
        def listener = new JFXBinderChangeListener(source, sourcePropertyName, target, targetPropertyName, converter)
        // blindly add the listener as Property does not expose a method to query existing listeners
        // javafx 2.2b17
        source."${sourcePropertyName}Property"().addListener(listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

}
