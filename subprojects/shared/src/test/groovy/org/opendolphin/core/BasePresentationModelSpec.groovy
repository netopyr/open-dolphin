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

package org.opendolphin.core

import spock.lang.Specification
import java.beans.PropertyChangeListener

class BasePresentationModelSpec extends Specification {
    def "attributes are accessible as properties"() {
        given:

        def baseAttribute = new MyAttribute('myPropName')
        def pm = new BasePresentationModel('1',[baseAttribute])

        expect:

        pm.attributes.find { it.propertyName == 'myPropName' } == baseAttribute // old style
        pm.myPropName == baseAttribute  // new style
    }

    def "missing attributes throw MissingPropertyException on access"() {
        given:
        def baseAttribute = new MyAttribute('myPropName')
        def pm = new BasePresentationModel('1',[baseAttribute])

        when:
        pm.noSuchAttributeName

        then:
        def exception = thrown(MissingPropertyException)
        exception.message.contains('noSuchAttributeName')
    }

    def "getValue(name,int) convenience method"() {
        given:
        def baseAttribute = new MyAttribute('myInt',1)
        def pm = new BasePresentationModel('1',[baseAttribute])

        expect:
        1 == pm.getValue('myInt', 0)
        0 == pm.getValue('no-such-property', 0)
    }

    def "finder methods"() {
        given:
        def baseAttribute = new MyAttribute('myInt',1)
        baseAttribute.qualifier = 'myQualifier'
        def pm = new BasePresentationModel('1',[baseAttribute])

        expect:
        null == pm.findAttributeByPropertyNameAndTag('myInt', null) // null safe
        null == pm.findAttributeByPropertyNameAndTag(null, null) // null safe

        null == pm.findAttributeByQualifier('no-such-qualifier')
        baseAttribute == pm.findAttributeByQualifier('myQualifier')

        null == pm.findAttributeById(Long.MAX_VALUE)
        baseAttribute == pm.findAttributeById(baseAttribute.id)
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