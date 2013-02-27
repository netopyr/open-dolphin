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

import groovy.beans.Bindable
import javafx.scene.paint.Color

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.bindInfo

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

        bindInfo 'value' of bean to 'text' of label

        then:

        assert label.text == 'Dolphin'
    }


    void testPojoBindingWithConverter() {
        given:

        def bean = new PojoBean(value: 'white')
        def label = new javafx.scene.control.Label()

        when:

        bindInfo 'value' of bean to 'textFill' of label, {it == 'white' ? Color.WHITE : Color.BLACK}

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
