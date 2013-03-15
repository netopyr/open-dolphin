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
import org.opendolphin.core.BasePresentationModel
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel

import static org.opendolphin.binding.JFXBinder.*

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

    void testPresentationModelBinding() {
        ClientPresentationModel sourceModel = new ClientPresentationModel('source', [new ClientAttribute('attr_1', "", null, Tag.MESSAGE)])
        def targetLabel = new javafx.scene.control.Label()

        bind 'attr_1', Tag.MESSAGE of sourceModel to 'text' of targetLabel
        sourceModel.getAt('attr_1', Tag.MESSAGE).value = 'dummy'
        assert targetLabel.text == 'dummy'
    }

    void testUnbindInfo() {
        ClientPresentationModel sourceModel = new ClientPresentationModel('source', [new ClientAttribute('text', "")])
        def targetLabel = new javafx.scene.control.Label()
        bindInfo 'dirty' of sourceModel to 'text' of targetLabel
        assert 'false' == targetLabel.text
        sourceModel.getAt('text').value = 'newValue'
        assert 'true' == targetLabel.text
        unbindInfo 'dirty' of sourceModel from 'text' of targetLabel
        sourceModel.getAt('text').value = ''
        assert 'true' == targetLabel.text
    }

    void testUnbindFromFX() {
        def sourceLabel = new javafx.scene.control.Label()
        ClientAttribute attribute = new ClientAttribute('text', '')
        bind 'text' of sourceLabel to 'value' of attribute
        sourceLabel.text = 'newValue'
        assert 'newValue' == attribute.value
        unbind 'text' of sourceLabel from 'value' of attribute
        sourceLabel.text = 'anotherValue'
        assert 'newValue' == attribute.value

    }

    void testUnbindFromClientPresentationModel() {
        def targetLabel = new javafx.scene.control.Label()
        ClientPresentationModel model = new ClientPresentationModel('model', [new ClientAttribute('attr', '')])
        bind 'attr' of model to 'text' of targetLabel
        model.getAt('attr').value = 'newValue'
        assert 'newValue' == targetLabel.text
        unbind 'attr' of model from 'text' of targetLabel
        model.getAt('attr').value = 'anotherValue'
        assert 'newValue' == targetLabel.text
    }

    void testUnbindFromPresentationModel() {
        def targetLabel = new javafx.scene.control.Label()
        PresentationModel model = new BasePresentationModel('model', [new ClientAttribute('attr', '')])
        bind 'attr' of model to 'text' of targetLabel
        model.getAt('attr').value = 'newValue'
        assert 'newValue' == targetLabel.text
        unbind 'attr' of model from 'text' of targetLabel
        model.getAt('attr').value = 'anotherValue'
        assert 'newValue' == targetLabel.text
    }


}

class PojoBean {
    @Bindable String value
}
