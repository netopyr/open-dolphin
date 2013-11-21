import cpm = require("../../js/dolphin/ClientPresentationModel");
import cmd = require("../../js/dolphin/Command");

export module dolphin {

    export class OnFinishedAdapter {
        onFinished(models: cpm.dolphin.ClientPresentationModel) : void { }
        onFinishedData(listOfData: any[]) : void { }
    }

    export class ClientConnector {

        send(command: cmd.dolphin.Command, onFinished: OnFinishedAdapter) {

            // prework

            // when ready, do the transmission
            var result = this.transmit([command]);

            // postwork

        }

        transmit(commands:cmd.dolphin.Command[]) : cmd.dolphin.Command[] {
            throw Error;// to be implemented in subclass
            return [];
        }
    }
}