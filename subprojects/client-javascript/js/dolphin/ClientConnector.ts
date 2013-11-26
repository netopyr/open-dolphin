import cpm = require("../../js/dolphin/ClientPresentationModel");
import cmd = require("../../js/dolphin/Command");

export module dolphin {

    export class OnFinishedAdapter {
        onFinished(models:cpm.dolphin.ClientPresentationModel[]):void {
        }

        onFinishedData(listOfData: any[]) : void { }
    }

    interface CommandAndHandler {
        command : cmd.dolphin.Command;
        handler : OnFinishedAdapter;
    }

    export interface Transmitter {
        transmit(commands:cmd.dolphin.Command[], onDone: (result: cmd.dolphin.Command[]) => void ) : void ;
    }

    export class ClientConnector {

        private commandQueue : CommandAndHandler[] = [];
        private currentlySending : boolean = false;
        private transmitter: Transmitter;

        constructor(transmitter:Transmitter) {
            this.transmitter = transmitter;
        }

        send(command: cmd.dolphin.Command, onFinished: OnFinishedAdapter) {
            this.commandQueue.push( {command: command, handler: onFinished } );
            if (this.currentlySending) return;
            this.doSendNext();
        }

        private doSendNext() {
            if (this.commandQueue.length < 1) {
                this.currentlySending = false;
                return;
            }
            this.currentlySending = true;
            var cmdAndHandler = this.commandQueue.shift();
            this.transmitter.transmit([cmdAndHandler.command], (result: cmd.dolphin.Command[]) => {
                console.log("in onDone ")

                // handle the result

                this.doSendNext();  // recursive call: fetch the next in line
            });
        }

    }
}