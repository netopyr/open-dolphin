import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.comm.InMemoryClientCommunicator
import com.canoo.dolphin.core.server.comm.Receiver

import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.client.comm.ValueChangedClientCommand
import com.canoo.dolphin.core.client.comm.ClientCommunicator
import com.canoo.dolphin.core.client.comm.ClientCommand
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.comm.AttributeCreatedCommand

class CommunicationTests extends GroovyTestCase {

    Receiver receiver
    ClientCommunicator communicator

    protected void setUp() {
        receiver = new Receiver() // no need to put the receiver behind a decoder since we are in-memory
        communicator = InMemoryClientCommunicator.instance
        communicator.receiver = receiver // inject receiver, could be a singleton (?)
    }

    void testSimpleAttributeChangeIsVisibleOnServer() {
        def ca = new ClientAttribute(TestBean, 'name')

        assert ca.communicator in InMemoryClientCommunicator
        assert ca.communicator.codec == null

        Command receivedCommand = null
        def testServerCommand = { ValueChangedClientCommand command ->
            receivedCommand = command
        }
        receiver.registry.register "ValueChanged", testServerCommand

        def initialBean = new TestBean(name: 'initial')
        ca.bean = initialBean

        assert receivedCommand.commandId == "ValueChanged"
        assert receivedCommand in ValueChangedClientCommand // since we are in-memory
        assert receivedCommand.oldValue == null
        assert receivedCommand.newValue == 'initial'
    }

    void testServerIsNotifiedAboutNewAttributesAndTheirPms() {
        def ca = new ClientAttribute(TestBean, 'name')

        Command receivedCommand = null
        def testServerCommand = { AttributeCreatedCommand command ->
            receivedCommand = command
        }
        receiver.registry.register "AttributeCreated", testServerCommand

        new ClientPresentationModel('testPm', [ca])

        assert receivedCommand.commandId == "AttributeCreated"
        assert receivedCommand in AttributeCreatedCommand // since we are in-memory
        assert receivedCommand.pmId == 'testPm'
        assert receivedCommand.propertyName == 'name'
        assert receivedCommand.attributeId

    }

    void testRequestingSomeGeneralCommandExecution() {
        boolean reached = false
        receiver.registry.register "ButtonAction", { reached = true }

        communicator.send(new ClientCommand(commandId: "ButtonAction"))
        assert reached
    }

}


class TestBean {
    String name
    String toString() {name}
}