import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.server.comm.Receiver

/**
 * Tests for the sequence between client requests and server responses.
 * They are really more integration tests than unit tests.
 */

class CommunicationTests extends GroovyTestCase {

	Receiver receiver
	ClientConnector communicator

	protected void setUp() {
		LogConfig.logCommunication()
		receiver = new Receiver() // no need to put the receiver behind a decoder since we are in-memory
		communicator = InMemoryClientConnector.instance
		communicator.receiver = receiver // inject receiver
	}

	void testSimpleAttributeChangeIsVisibleOnServer() {
		def ca = new ClientAttribute('name')

		assert ca.communicator in InMemoryClientConnector
		assert ca.communicator.codec == null

		Command receivedCommand = null
		def testServerAction = { ValueChangedCommand command, response ->
			receivedCommand = command
		}
		receiver.registry.register ValueChangedCommand, testServerAction

		ca.value = 'initial'

		assert receivedCommand.id == 'ValueChanged'
		assert receivedCommand in ValueChangedCommand
		assert receivedCommand.oldValue == null
		assert receivedCommand.newValue == 'initial'
	}

	void testServerIsNotifiedAboutNewAttributesAndTheirPms() {
		def ca = new ClientAttribute('name')

		Command receivedCommand = null
		def testServerAction = { AttributeCreatedCommand command, response ->
			receivedCommand = command
		}
		receiver.registry.register AttributeCreatedCommand, testServerAction

		new ClientPresentationModel('testPm', [ca])

		assert receivedCommand.id == "AttributeCreated"
		assert receivedCommand in AttributeCreatedCommand
		assert receivedCommand.pmId == 'testPm'
		assert receivedCommand.propertyName == 'name'
		assert receivedCommand.attributeId
	}

	void testWhenServerChangesValueThisTriggersUpdateOnClient() {
		def ca = new ClientAttribute('name')

		def setValueAction = { AttributeCreatedCommand command, response ->
			response << new ValueChangedCommand(
					attributeId: command.attributeId,
					newValue: "set from server",
					oldValue: null
			)
		}
		receiver.registry.register AttributeCreatedCommand, setValueAction

		Command receivedCommand = null
		def valueChangedAction = { ValueChangedCommand command, response ->
			receivedCommand = command
		}
		receiver.registry.register ValueChangedCommand, valueChangedAction

		new ClientPresentationModel('testPm', [ca]) // trigger the whole cycle

		assert ca.value == "set from server"	// client is updated

		assert receivedCommand.attributeId == ca.id // client notified server about value change
		// todo: we may later want to shortcut the above for the sake of efficiency
	}

	void testRequestingSomeGeneralCommandExecution() {
		boolean reached = false
		receiver.registry.register "ButtonAction", { cmd, resp -> reached = true }

		communicator.send(new NamedCommand(id: "ButtonAction"))
		assert reached
	}

	void testWritingToASwitchAlsoWritesBackToTheSource() {
		def switchPm = new ClientPresentationModel([new ClientAttribute('name')])
		def sourcePm = new ClientPresentationModel([new ClientAttribute('name')])

		// this is supposed to become a default action on the server side
		def valueChangedAction = { ValueChangedCommand command, response ->
			response << command
		}
		receiver.registry.register ValueChangedCommand, valueChangedAction

		// switches need to set both, id and value!
		switchPm.name.id = sourcePm.name.id
		switchPm.name.value = sourcePm.name.value

        assert sourcePm.name.value == null
		switchPm.name.value = 'newValue'
		assert sourcePm.name.value == 'newValue'
	}

	void testWritingToTheSourceAlsoUpdatesTheSwitch() {
		def switchPm = new ClientPresentationModel([new ClientAttribute('name')])
		def sourcePm = new ClientPresentationModel([new ClientAttribute('name')])

		// this is supposed to become a default action on the server side
		def valueChangedAction = { ValueChangedCommand command, response ->
			response << command
		}
		receiver.registry.register ValueChangedCommand, valueChangedAction

		// switches need to set both, id and value!
		switchPm.name.id = sourcePm.name.id
		switchPm.name.value = sourcePm.name.value

        assert switchPm.name.value == null
		sourcePm.name.value = 'newValue'
		assert switchPm.name.value == 'newValue'
	}

	void testWritingToSwitchesWithSwitchingSources() {
		def switchPm = new ClientPresentationModel([new ClientAttribute('name')])
		def sourcePm = new ClientPresentationModel([new ClientAttribute('name')])
		def otherPm  = new ClientPresentationModel([new ClientAttribute('name')])

		// this is supposed to become a default action on the server side
		def valueChangedAction = { ValueChangedCommand command, response ->
			response << command
		}
		receiver.registry.register ValueChangedCommand, valueChangedAction

		// switches need to set both, id and value!
		switchPm.name.id = sourcePm.name.id
		switchPm.name.value = sourcePm.name.value

		switchPm.name.value = 'firstValue'
		assert sourcePm.name.value == 'firstValue'
        assert otherPm.name.value  == null           // untouched

		// switches need to set both, id and value!
		switchPm.name.id = otherPm.name.id
		switchPm.name.value = otherPm.name.value

        assert switchPm.name.value == null
        assert sourcePm.name.value == 'firstValue'   // untouched

		// updating the selection should update the referred-to attribute but not the old one
		switchPm.name.value = 'secondValue'
		assert sourcePm.name.value == 'firstValue'   // untouched
		assert otherPm.name.value == 'secondValue'

		otherPm.name.value = 'otherValue'
		assert switchPm.name.value == 'otherValue'
	}

}
