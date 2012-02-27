import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.comm.InMemoryClientCommunicator
import com.canoo.dolphin.core.server.comm.Receiver
import com.canoo.dolphin.core.server.comm.ValueChangedServerCommand
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.client.comm.ValueChangedClientCommand

class CommunicationTests extends GroovyTestCase {

    void testSimpleAttributeChangeIsVisibleOnServer() {
        def ca = new ClientAttribute(TestBean, 'name')

        assert ca.communicator in InMemoryClientCommunicator
        assert ca.communicator.codec == null

        def receiver = new Receiver()       // no need to put the receiver behind a decoder since we are in-memory
        def testServerCommand = new TestValueChangedServerCommand()
        receiver.registry.register("ValueChanged", testServerCommand)
        ca.communicator.receiver = receiver // inject receiver, could be a singleton (?)

        def initialBean = new TestBean(name: 'initial')
        ca.bean = initialBean

        assert testServerCommand.receivedCommand.commandId == "ValueChanged"
        assert testServerCommand.receivedCommand in ValueChangedClientCommand // since we are in-memory
        assert testServerCommand.receivedCommand.oldValue == null
        assert testServerCommand.receivedCommand.newValue == 'initial'

    }

    
}

class TestValueChangedServerCommand extends ValueChangedServerCommand {
    Command receivedCommand = null
    def call(Command command) { super.call(command); receivedCommand = command }
}

class TestBean {
    String name
    String toString() {name}
}