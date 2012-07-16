package com.canoo.dolphin.core.comm

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

import com.canoo.dolphin.core.server.comm.Receiver
import com.canoo.dolphin.core.client.comm.UiThreadHandler
import com.canoo.dolphin.core.client.ClientModelStore

/**
 * Tests for the sequence between client requests and server responses.
 * They are really more integration tests than unit tests.
 */

class CommunicationTests extends GroovyTestCase {

	Receiver receiver
	ClientConnector communicator
    ClientModelStore clientModelStore

	protected void setUp() {
		LogConfig.logCommunication()
		receiver = new Receiver() // no need to put the receiver behind a decoder since we are in-memory
		communicator = InMemoryClientConnector.instance
        communicator.processAsync = false
        communicator.uiThreadHandler = { it() } as UiThreadHandler
		communicator.receiver = receiver // inject receiver
        clientModelStore = new ClientModelStore(communicator)
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
		def testServerAction = { CreatePresentationModelCommand command, response ->
			receivedCommand = command
		}
		receiver.registry.register CreatePresentationModelCommand, testServerAction

		clientModelStore.add new ClientPresentationModel('testPm', [ca])

		assert receivedCommand.id == "CreatePresentationModel"
		assert receivedCommand instanceof CreatePresentationModelCommand
		assert receivedCommand.pmId == 'testPm'
		assert receivedCommand.attributes.name
	}

	void testWhenServerChangesValueThisTriggersUpdateOnClient() {
		def ca = new ClientAttribute('name')

		def setValueAction = { CreatePresentationModelCommand command, response ->
			response << new ValueChangedCommand(
					attributeId: command.attributes.id.first(),
					newValue: "set from server",
					oldValue: null
			)
		}
		receiver.registry.register CreatePresentationModelCommand, setValueAction

		Command receivedCommand = null
		def valueChangedAction = { ValueChangedCommand command, response ->
			receivedCommand = command
		}
		receiver.registry.register ValueChangedCommand, valueChangedAction

        clientModelStore.add new ClientPresentationModel('testPm', [ca]) // trigger the whole cycle

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

}
