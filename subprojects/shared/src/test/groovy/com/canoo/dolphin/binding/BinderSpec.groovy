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

import com.canoo.dolphin.core.BasePresentationModel
import spock.lang.Specification

import static com.canoo.dolphin.binding.Binder.bind
import static com.canoo.dolphin.binding.Binder.unbind
import groovy.beans.Bindable

class BinderSpec extends Specification {
    def 'bind and unbind on POJOs'() {
        given:
        def initialValue = 'pojo'
        def sourcePojo = new Pojo(value: initialValue)
        def targetPojo = new Pojo()

        when:

        bind 'value' of sourcePojo to 'value' of targetPojo

        then:

        // values are sync immediately when bound
        targetPojo.value == sourcePojo.value

        when:

        sourcePojo.value = 'newValue'

        then:

        // values are sync on source change
        targetPojo.value == sourcePojo.value

        when:

        unbind 'value' of sourcePojo from 'value' of targetPojo
        sourcePojo.value = 'anotherValue'

        then:

        sourcePojo.value == 'anotherValue'
        targetPojo.value != sourcePojo.value
        targetPojo.value == 'newValue'

        !sourcePojo.getPropertyChangeListeners('value').find() {it instanceof BinderPropertyChangeListener}
    }

    def 'bind and unbind on POJOs and PMs'() {
        given:
        def initialValue = 'pojo'
        def sourcePm = new BasePresentationModel([new SimpleAttribute('text')])
        sourcePm.text.value = initialValue
        def targetPojo = new Pojo()

        when:

        bind 'text' of sourcePm to 'value' of targetPojo

        then:

        // values are sync immediately when bound
        targetPojo.value == sourcePm.text.value

        when:

        sourcePm.text.value = 'newValue'

        then:

        // values are sync on source change
        targetPojo.value == sourcePm.text.value

        when:

        unbind 'text' of sourcePm from 'value' of targetPojo
        sourcePm.text.value = 'anotherValue'

        then:

        sourcePm.text.value == 'anotherValue'
        targetPojo.value != sourcePm.text.value
        targetPojo.value == 'newValue'

        !sourcePm.text.getPropertyChangeListeners('value').find() {it instanceof BinderPropertyChangeListener}
    }
}