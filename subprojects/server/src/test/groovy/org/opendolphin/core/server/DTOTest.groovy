package org.opendolphin.core.server

import static org.opendolphin.core.server.Slot.slots;

public class DTOTest extends GroovyTestCase {

    void testGroovyFriendlyCtor() {
        new DTO( slots(a:1, b:2) )
    }

}
