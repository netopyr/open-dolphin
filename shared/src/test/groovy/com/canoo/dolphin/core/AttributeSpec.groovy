package com.canoo.dolphin.core

import spock.lang.Specification
import java.beans.PropertyChangeListener

class AttributeSpec extends Specification {

    def "simple constructor with null bean and null value"() {
        when:
            def attribute = new BaseAttribute(TestBean, "name")
        then:
            attribute.bean  == null
            attribute.value == null
    }

    def "false constructor calls"() {
        when:
            new BaseAttribute(beanType, propName)
        then:
            thrown(AssertionError)
        where:
            beanType | propName
            null     | null
            null     | 'name'
            TestBean | null
            TestBean | 'no-such-property'
    }

    void "listener is notified when an attribute value changes"() {
        given:
            def attribute = new BaseAttribute(TestBean,'name')
            attribute.value = ""
            def changeListener = Mock(PropertyChangeListener)
        when:
            attribute.addPropertyChangeListener("value", changeListener)
            attribute.value = 'newValue'
        then:
            1 * changeListener.propertyChange(_)
    }
    
    void "when the bean is set, the new value must be set"() {
        given:
            def attribute = new BaseAttribute(TestBean,'name')
            attribute.value = ""
            def newBean = new TestBean(name: 'newName')
        when:
            attribute.bean = newBean
        then:
            attribute.value == 'newName'
    }

    void "when the new bean is null, the new value must be null"() {
        given:
            def attribute = new BaseAttribute(TestBean,'name')
            attribute.value = ""
        when:
            attribute.bean = null
        then:
            attribute.value == null
    }

}

