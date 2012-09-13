/*
 * Copyright 2012 Canoo Engineering AG.
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