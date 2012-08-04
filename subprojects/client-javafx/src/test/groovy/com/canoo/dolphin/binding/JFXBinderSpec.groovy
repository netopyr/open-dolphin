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