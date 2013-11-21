import tsUnit = require("../../testsuite/tsUnit")
import cc     = require("../../js/dolphin/ClientConnector")
import cmd    = require("../../js/dolphin/Command")


export module dolphin {

    class TestClientConnector extends cc.dolphin.ClientConnector {
        constructor(public clientCommands, public serverCommands) {
            super()
        }

        transmit(commands:cmd.dolphin.Command[]) : cmd.dolphin.Command[] {
            this.clientCommands = commands;
            return this.serverCommands;
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

    }
}