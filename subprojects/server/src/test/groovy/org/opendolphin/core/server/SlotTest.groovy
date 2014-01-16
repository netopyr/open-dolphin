package org.opendolphin.core.server

import org.opendolphin.core.Tag
import spock.lang.Specification

class SlotTest extends Specification {

    void "default ctor values for slots"() {
        given:
        def slot = new Slot("myproperty", value)

        expect:
        slot.value      == value
        slot.baseValue  == baseValue
        slot.qualifier  == qualifier
        slot.tag        == tag

        where:
        value | baseValue | qualifier | tag
        null  | null      | null      | Tag.VALUE
        1     | 1         | null      | Tag.VALUE
        "x"   | "x"       | null      | Tag.VALUE
    }
}
