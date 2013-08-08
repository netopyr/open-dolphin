package org.opendolphin.core.server

class ServerAttributeTest extends GroovyTestCase{

    void testSetIdOnce() {
        def attribute = new ServerAttribute("a", 0)
        attribute.setId(123l)
        assert 123l == attribute.getId()
    }

    void testSetIdTwiceFails() {
        def attribute = new ServerAttribute("a", 0)
        attribute.setId(123l)

        shouldFail(IllegalStateException) {
            attribute.setId(312l)
        }
    }
}
