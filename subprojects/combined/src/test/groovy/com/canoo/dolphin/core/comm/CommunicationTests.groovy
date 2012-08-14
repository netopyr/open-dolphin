package com.canoo.dolphin.core.comm

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientModelStore
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.server.comm.ServerConnector

/**
 * Tests for the sequence between client requests and server responses.
 * They are really more integration tests than unit tests.
 */

class CommunicationTests extends GroovyTestCase {

	ServerConnector serverConnector
	ClientConnector clientConnector
    ClientModelStore clientModelStore

	protected void setUp() {
		LogConfig.logCommunication()
		def config = new TestInMemoryConfig()
        config.clientDolphin.clientConnector.processAsync = false
        serverConnector = config.serverDolphin.serverConnector
        clientConnector = config.clientDolphin.clientConnector
        clientModelStore = config.clientDolphin.clientModelStore

	}

	void testSimpleAttributeChangeIsVisibleOnServer() {
		def ca = new ClientAttribute('name')
        def cpm = new ClientPresentationModel('model', [ca])
        clientModelStore.add cpm

		Command receivedCommand = null
		def testServerAction = { ValueChangedCommand command, response ->
			receivedCommand = command
		}
		serverConnector.registry.register ValueChangedCommand, testServerAction

		ca.value = 'initial'

        assert receivedCommand
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
		serverConnector.registry.register CreatePresentationModelCommand, testServerAction

		clientModelStore.add new ClientPresentationModel('testPm', [ca]) // todo dk: this should be automatic!

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
		serverConnector.registry.register CreatePresentationModelCommand, setValueAction

		Command receivedCommand = null
		def valueChangedAction = { ValueChangedCommand command, response ->
			receivedCommand = command
		}
		serverConnector.registry.register ValueChangedCommand, valueChangedAction

        clientModelStore.add new ClientPresentationModel('testPm', [ca]) // trigger the whole cycle

		assert ca.value == "set from server"	// client is updated

		assert receivedCommand.attributeId == ca.id // client notified server about value change
		// todo: we may later want to shortcut the above for the sake of efficiency
	}

	void testRequestingSomeGeneralCommandExecution() {
        println 1
		boolean reached = false
        println 2
		serverConnector.registry.register "ButtonAction", { cmd, resp -> reached = true }
        println 3
		clientConnector.send(new NamedCommand(id: "ButtonAction"))
        println 4
		assert reached
        println 5
	}

}
