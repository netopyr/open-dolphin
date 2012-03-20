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

	void testSwitch() {
		def selectedPM = new ClientPresentationModel([new ClientAttribute('name')])
		def pm1 = new ClientPresentationModel([new ClientAttribute('name')])
		def pm2 = new ClientPresentationModel([new ClientAttribute('name')])

		// this is supposed to become a default action on the server side
		def valueChangedAction = { ValueChangedCommand command, response ->
			response << command
		}
		receiver.registry.register ValueChangedCommand, valueChangedAction

		// switches need to set both, id and value!
		selectedPM.name.id = pm1.name.id
		selectedPM.name.value = pm1.name.value

		selectedPM.name.value = 'firstValue'
		assert pm1.name.value == 'firstValue'

		// switches need to set both, id and value!
		selectedPM.name.id = pm2.name.id
		selectedPM.name.value = pm2.name.value

		// updating the selection should update the referred-to attribute but not the old one
		selectedPM.name.value = 'secondValue'
		assert pm1.name.value == 'firstValue'
		assert pm2.name.value == 'secondValue'

		pm2.name.value = 'otherValue'
		assert selectedPM.name.value == 'otherValue'
	}

}
