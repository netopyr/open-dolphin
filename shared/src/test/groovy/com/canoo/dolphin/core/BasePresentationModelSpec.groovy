package com.canoo.dolphin.core

import java.beans.PropertyChangeListener
import spock.lang.Specification

class BasePresentationModelSpec extends Specification {
    def "attributes are accessible as properties"() {
        given:

        def baseAttribute = new BaseAttribute('myPropName')
        def pm = new BasePresentationModel([baseAttribute])

        expect:

        pm.attributes.find { it.propertyName == 'myPropName' } == baseAttribute // old style
        pm.myPropName == baseAttribute  // new style
    }

    def "missing attributes throw MissingPropertyException on access"() {
        given:
        def baseAttribute = new BaseAttribute('myPropName')
        def pm = new BasePresentationModel([baseAttribute])

        when:
        pm.noSuchAttributeName

        then:
        def exception =  thrown(MissingPropertyException)
        exception.message.contains('noSuchAttributeName')
        exception.message.contains('myPropName')
    }


}