package com.canoo.dolphin.core.server

import static com.canoo.dolphin.binding.Binder.bind

public class DomainObjectBindTest extends GroovyTestCase {

    void testHappyPath() {
        given:

        def att = new ServerAttribute(TestBean, "name")
        def pm = new ServerPresentationModel([att])
        def initialValue = "Andres&Dierk"
        def srcBean = new TestBean(name: initialValue)
        att.bean = srcBean

        def targetBean = new TestBean()
        assert targetBean.name == null

        when:

        bind "name" of pm to "name" of targetBean

        assert targetBean.name == initialValue

        def newValue = "newValue"
        att.value = newValue

        then:

        assert targetBean.name == newValue
    }
}