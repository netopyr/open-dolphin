package com.canoo.dolphin.binding

import static com.canoo.dolphin.binding.JFXBinder.bind

class JFXBinderTest extends GroovyTestCase {

    void testNodeBinding() {
        given:
        def initialValue = "Andres&Dierk"
        def sourceLabel = new javafx.scene.control.Label()
        sourceLabel.text = initialValue
        def targetLabel = new javafx.scene.control.Label()

        assert ! targetLabel.text

        when:
        bind "text" of sourceLabel to "text" of targetLabel

        assert targetLabel.text == initialValue

        def newValue = "newValue"
        sourceLabel.text = newValue

        then:
        assert targetLabel.text == newValue
    }
}
