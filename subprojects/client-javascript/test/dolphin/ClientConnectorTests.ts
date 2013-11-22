import tsUnit = require("../../testsuite/tsUnit")
import cc     = require("../../js/dolphin/ClientConnector")
import cmd    = require("../../js/dolphin/Command")


export module dolphin {

    class TestClientConnector extends cc.dolphin.ClientConnector {
        constructor(public clientCommands, public serverCommands) {
            super()
        }

        transmit(commands:cmd.dolphin.Command[], onDone: (result: cmd.dolphin.Command[]) => void ) : void {
            this.clientCommands = commands;
            onDone(this.serverCommands);
        }
    }

    export class ClientConnectorTests extends tsUnit.tsUnit.TestClass {

        sendingOneCommandMustCallTheTransmission() {
            var singleCommand   = new cmd.dolphin.Command();
            var clientConnector = new TestClientConnector(undefined, undefined)

            clientConnector.send(singleCommand, undefined)

            this.areIdentical( clientConnector.clientCommands.length, 1)
            this.areIdentical( clientConnector.clientCommands[0], singleCommand)
        }

        sendingMultipleCommands() {
            var singleCommand   = new cmd.dolphin.Command();
            var lastCommand   = new cmd.dolphin.Command();
            var clientConnector = new TestClientConnector(undefined, undefined)

            clientConnector.send(singleCommand, undefined)
            clientConnector.send(singleCommand, undefined)
            clientConnector.send(lastCommand, undefined)

            this.areIdentical( clientConnector.clientCommands.length, 1)
            this.areIdentical( clientConnector.clientCommands[0], lastCommand)
        }

    }
}