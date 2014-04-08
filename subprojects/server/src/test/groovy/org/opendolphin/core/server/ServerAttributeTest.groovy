package org.opendolphin.core.server

class ServerAttributeTest extends GroovyTestCase{

    void testSetIdOnce() {
        def attribute = new ServerAttribute("a", 0)
        assert attribute.getId().endsWith("S")
    }

}
