package com.canoo.dolphin.binding

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.BasePresentationModel
import groovy.beans.Bindable

import static com.canoo.dolphin.binding.Binder.bind

class BinderTest extends GroovyTestCase {
    void testPojoBinding() {
        given:
        def initialValue = "Andres&Dierk"
        def sourcePojo = new Pojo(value: initialValue)
        def targetPojo = new Pojo()

        assert !targetPojo.value

        when:
        bind "value" of sourcePojo to "value" of targetPojo

        assert targetPojo.value == initialValue

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == newValue
    }

    void testAttributeBinding() {
        given:
        def initialValue = "Andres&Dierk"
        def sourcePm = new BasePresentationModel([new SimpleAttribute('text')])
        sourcePm.text.value = initialValue
        def targetPojo = new Pojo()

        assert !targetPojo.value

        when:
        bind "text" of sourcePm to "value" of targetPojo

        assert targetPojo.value == initialValue

        def newValue = "newValue"
        sourcePm.text.value = newValue

        then:
        assert targetPojo.value == newValue
    }
}

class Pojo {
    @Bindable String value
}

class SimpleAttribute extends BaseAttribute {
    SimpleAttribute(String propertyName) {
        super(propertyName)
    }

    SimpleAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }
}