package com.canoo.dolphin.core.comm

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.server.comm.Receiver

/**
 * Tests for the approach of using plain attributes as switches by sharing the id and
 * letting server roundtrip care for distribution of value changes.
 */
class AttributeSwitchTests extends GroovyTestCase {

    ClientPresentationModel switchPm
    ClientPresentationModel sourcePm

    protected void setUp() {
        LogConfig.logCommunication()
        def receiver = new Receiver()
        def communicator = InMemoryClientConnector.instance
        communicator.receiver = receiver

        switchPm = new ClientPresentationModel([new ClientAttribute('name')])
        sourcePm = new ClientPresentationModel([new ClientAttribute('name')])

        // this is supposed to become a default action on the server side
        def valueChangedAction = { ValueChangedCommand command, response ->
            response << command
        }
        receiver.registry.register ValueChangedCommand, valueChangedAction
    }

    /** switching needs to set both, id and value! **/
    protected void switchNameAttributeTo(ClientPresentationModel switchPM, ClientPresentationModel source) {
        switchPM.name.id = source.name.id
        switchPM.name.value = source.name.value
    }

    void testWritingToASwitchAlsoWritesBackToTheSource() {
        assert switchPm.name.value == null  //
        assert sourcePm.name.value == null

        switchNameAttributeTo switchPm, sourcePm

        assert switchPm.name.value == null
        assert sourcePm.name.value == null

        switchPm.name.value = 'newValue'

        assert sourcePm.name.value == 'newValue'
    }

    void testWritingToTheSourceAlsoUpdatesTheSwitch() {

        switchNameAttributeTo switchPm, sourcePm

        sourcePm.name.value = 'newValue'

        assert switchPm.name.value == 'newValue'
    }

    void testWritingToSwitchesWithSwitchingSources() {

        def otherPm = new ClientPresentationModel([new ClientAttribute('name')])

        switchNameAttributeTo switchPm, sourcePm

        switchPm.name.value = 'firstValue'

        assert sourcePm.name.value == 'firstValue'
        assert otherPm.name.value == null           // untouched

        switchNameAttributeTo switchPm, otherPm

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
