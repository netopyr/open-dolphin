/*
 * Copyright 2012-2013 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.binding

import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.BasePresentationModel
import groovy.beans.Bindable

import static org.opendolphin.binding.Binder.bind

class BinderTest extends GroovyTestCase {
    void testPojoBinding() {
        given:
        def initialValue = "Andres&Dierk"
        def sourcePojo = new BindablePojo(value: initialValue)
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "value" of sourcePojo to "value" of targetPojo

        assert targetPojo.value == initialValue

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == newValue
    }

    void testPojoBindingWithConverter_Closure() {
        given:
        def sourcePojo = new BindablePojo(value: 'initialValue')
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "value" of sourcePojo to "value" of targetPojo, { "[" + it + "]"}

        assert targetPojo.value == "[initialValue]"

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == "[newValue]"
    }

    void testPojoBindingWithConverter_Interface() {
        given:
        def sourcePojo = new BindablePojo(value: 'initialValue')
        def targetPojo = new BindablePojo()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        assert !targetPojo.value

        when:
        bind "value" of sourcePojo to "value" of targetPojo, converter

        assert targetPojo.value == "[initialValue]"

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == "[newValue]"
    }

    void testAttributeBinding() {
        given:
        def initialValue = "Andres&Dierk"
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text')])
        sourcePm.text.value = initialValue
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "text" of sourcePm to "value" of targetPojo

        assert targetPojo.value == initialValue

        def newValue = "newValue"
        sourcePm.text.value = newValue

        then:
        assert targetPojo.value == newValue
    }

    void testAttributeBindingWithConverter_Closure() {
        given:
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text', 'initialValue')])
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "text" of sourcePm to "value" of targetPojo, {"[" + it + "]"}

        assert targetPojo.value == "[initialValue]"

        sourcePm.text.value = "newValue"

        then:
        assert targetPojo.value == "[newValue]"
    }

    void testAttributeBindingWithConverter_Interface() {
        given:
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text', 'initialValue')])
        def targetPojo = new BindablePojo()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        assert !targetPojo.value

        when:
        bind "text" of sourcePm to "value" of targetPojo, converter

        assert targetPojo.value == "[initialValue]"

        sourcePm.text.value = "newValue"

        then:
        assert targetPojo.value == "[newValue]"
    }

}

class BindablePojo {
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