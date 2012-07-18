package com.canoo.dolphin.binding

import groovy.beans.Bindable
import javafx.scene.paint.Color

import static com.canoo.dolphin.binding.JFXBinder.bind

class JFXBinderTest extends GroovyTestCase {

    void testNodeBinding() {
        given:
        def initialValue = "Andres&Dierk"
        def sourceLabel = new javafx.scene.control.Label()
        sourceLabel.text = initialValue
        def targetLabel = new javafx.scene.control.Label()

        assert !targetLabel.text

        when:
        bind "text" of sourceLabel to "text" of targetLabel

        assert targetLabel.text == initialValue

        def newValue = "newValue"
        sourceLabel.text = newValue

        then:
        assert targetLabel.text == newValue
    }

    void testPojoBinding() {
        given:

        def bean = new PojoBean(value: 'Dolphin')
        def label = new javafx.scene.control.Label()

        when:

        bind 'value' of bean to 'text' of label

        then:

        assert label.text == 'Dolphin'
    }


    void testPojoBindingWithConverter() {
        given:

        def bean = new PojoBean(value: 'white')
        def label = new javafx.scene.control.Label()

        when:

        bind 'value' of bean to 'textFill' of label, {it == 'white' ? Color.WHITE : Color.BLACK}

        then:

        assert label.textFill == Color.WHITE

        nextWhen:

        bean.value = 'foo'

        nextThen:

        assert label.textFill == Color.BLACK
    }

}

class PojoBean {
    @Bindable String value
}
