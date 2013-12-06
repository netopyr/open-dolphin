import tsUnit = require("../../testsuite/tsUnit")
import cc     = require("../../js/dolphin/ClientConnector")
import hcc    = require("../../js/dolphin/HttpTransmitter")
import cmd    = require("../../js/dolphin/Command")


export module dolphin {

    class TestTransmitter implements cc.dolphin.Transmitter {
        constructor(public clientCommands, public serverCommands) {
        }

        transmit(commands:cmd.dolphin.Command[], onDone: (result: cmd.dolphin.Command[]) => void ) : void {
            this.clientCommands = commands;
            onDone(this.serverCommands);
        }
    }

    export class ClientConnectorTests extends tsUnit.tsUnit.TestClass {

        sendingOneCommandMustCallTheTransmission() {
            var singleCommand   = new cmd.dolphin.Command();
            var transmitter     = new TestTransmitter(undefined, undefined)
            var clientConnector = new cc.dolphin.ClientConnector(transmitter);

            clientConnector.send(singleCommand, undefined)

            this.areIdentical( transmitter.clientCommands.length, 1)
            this.areIdentical( transmitter.clientCommands[0], singleCommand)
        }

        sendingMultipleCommands() {
            var singleCommand   = new cmd.dolphin.Command();
            var lastCommand     = new cmd.dolphin.Command();
            var transmitter     = new TestTransmitter(undefined, undefined)
            var clientConnector = new cc.dolphin.ClientConnector(transmitter);

            clientConnector.send(singleCommand, undefined)
            clientConnector.send(singleCommand, undefined)
            clientConnector.send(lastCommand, undefined)

            this.areIdentical( transmitter.clientCommands.length, 1)
            this.areIdentical( transmitter.clientCommands[0], lastCommand)
        }

        tryCanoo() {
            var singleCommand   = new cmd.dolphin.Command();
            var lastCommand     = new cmd.dolphin.Command();
            var transmitter = new hcc.dolphin.HttpTransmitter('http://localhost:8080/dolphin-grails/dolphin/');

            var clientConnector = new cc.dolphin.ClientConnector(transmitter);

            clientConnector.send(singleCommand, undefined)
            clientConnector.send(singleCommand, undefined)
            clientConnector.send(lastCommand, undefined)

        }

    }
}