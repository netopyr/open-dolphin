package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel


/**
 * Tests for the approach of using plain attributes as switches by sharing the id.
 */

class AttributeSwitchTests extends GroovyTestCase {

    ClientPresentationModel switchPm
    ClientPresentationModel sourcePm

    protected void setUp() {
        switchPm = new ClientPresentationModel([new ClientAttribute('name')])
        sourcePm = new ClientPresentationModel([new ClientAttribute('name')])
    }

    /** switching needs to set both, id and value! **/

    void testWritingToASwitchAlsoWritesBackToTheSource() {
        assert switchPm.name.value == null  //
        assert sourcePm.name.value == null

        switchPm.name.syncWith sourcePm.name

        assert switchPm.name.value == null
        assert sourcePm.name.value == null

        switchPm.name.value = 'newValue'

        assert sourcePm.name.value == 'newValue'
    }

    void testWritingToTheSourceAlsoUpdatesTheSwitch() {

        switchPm.name.syncWith sourcePm.name

        sourcePm.name.value = 'newValue'

        assert switchPm.name.value == 'newValue'
    }

    void testWritingToSwitchesWithSwitchingSources() {

        def otherPm = new ClientPresentationModel([new ClientAttribute('name')])

        switchPm.name.syncWith sourcePm.name

        switchPm.name.value = 'firstValue'

        assert sourcePm.name.value == 'firstValue'
        assert otherPm.name.value == null           // untouched

        switchPm.name.syncWith otherPm.name

        assert switchPm.name.value == null
        assert sourcePm.name.value == 'firstValue'   // untouched

        // updating the selection should update the referred-to attribute but not the old one
        switchPm.name.value = 'secondValue'
        assert sourcePm.name.value == 'firstValue'   // untouched
        assert otherPm.name.value == 'secondValue'

        // updating the new source should update the switch but not the no-longer-referred-to source
        otherPm.name.value = 'otherValue'
        assert switchPm.name.value == 'otherValue'
        assert sourcePm.name.value == 'firstValue'
    }
}
