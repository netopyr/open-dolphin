package com.canoo.dolphin.core

import spock.lang.Specification
import java.beans.PropertyChangeListener

class BasePresentationModelSpec extends Specification {
    def "attributes are accessible as properties"() {
        given:

        def baseAttribute = new MyAttribute('myPropName')
        def pm = new BasePresentationModel([baseAttribute])

        expect:

        pm.attributes.find { it.propertyName == 'myPropName' } == baseAttribute // old style
        pm.myPropName == baseAttribute  // new style
    }

    def "missing attributes throw MissingPropertyException on access"() {
        given:
        def baseAttribute = new MyAttribute('myPropName')
        def pm = new BasePresentationModel([baseAttribute])

        when:
        pm.noSuchAttributeName

        then:
        def exception = thrown(MissingPropertyException)
        exception.message.contains('noSuchAttributeName')
    }

    def "dirty attributes make the pm dirty too"() {
        given:

        def attr1 = new MyAttribute('one')
        def attr2 = new MyAttribute('two', 2)
        def model = new BasePresentationModel('model', [attr1, attr2])
        def changeListener = Mock(PropertyChangeListener)
        model.addPropertyChangeListener(PresentationModel.DIRTY_PROPERTY, changeListener)

        assert !attr1.dirty
        assert !attr2.dirty
        assert !model.dirty

        when:

        attr1.value = 1

        then:

        1 * changeListener.propertyChange(_)
        attr1.dirty
        model.dirty

        when:

        attr1.value = null

        then:

        1 * changeListener.propertyChange(_)
        !attr1.dirty
        !model.dirty

        when:

        attr2.value = 2

        then:

        0 * changeListener.propertyChange(_)
        !attr2.dirty
        !model.dirty

        when:

        attr2.value = 3

        then:

        1 * changeListener.propertyChange(_)
        attr2.dirty
        model.dirty

        when:

        attr1.value = 4

        then:

        0 * changeListener.propertyChange(_)
        attr1.dirty
        attr2.dirty
        model.dirty
    }
}