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

import spock.lang.Specification

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JFXBinder.unbind
import groovy.beans.Bindable

class JFXBinderSpec extends Specification {
    def 'bind and unbind jfx Node to POJO'() {
        given:
        def initialValue = 'pojo'
        def sourceNode = new javafx.scene.control.Label()
        sourceNode.text = initialValue
        def targetPojo = new Pojo2()

        when:

        bind 'text' of sourceNode to 'value' of targetPojo

        then:

        // values are sync immediately when bound
        targetPojo.value == sourceNode.text

        when:

        sourceNode.text = 'newValue'

        then:

        // values are sync on source change
        targetPojo.value == sourceNode.text

        when:

        unbind 'text' of sourceNode from 'value' of targetPojo
        sourceNode.text = 'anotherValue'

        then:

        sourceNode.text == 'anotherValue'
        targetPojo.value != sourceNode.text
        targetPojo.value == 'newValue'
    }
}

class Pojo2 {
    @Bindable String value
}