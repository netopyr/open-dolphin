import cpm    = require("../../js/dolphin/ClientPresentationModel");
import cmd    = require("../../js/dolphin/Command");
import cod    = require("../../js/dolphin/Codec");
import nca    = require("../../js/dolphin/CallNamedActionCommand");
import cd     = require("../../js/dolphin/ClientDolphin");
import amdcc  = require("../../js/dolphin/AttributeMetadataChangedCommand");
import ca     = require("../../js/dolphin/ClientAttribute");

export module dolphin {

    export class OnFinishedAdapter {
        onFinished(models:cpm.dolphin.ClientPresentationModel[]):void {
        }

        onFinishedData(listOfData:any[]):void {
        }
    }

    interface CommandAndHandler {
        command : cmd.dolphin.Command;
        handler : OnFinishedAdapter;
    }

    export interface Transmitter {
        transmit(commands:cmd.dolphin.Command[], onDone:(result:cmd.dolphin.Command[]) => void) : void ;
    }

    export class ClientConnector {

        private commandQueue:CommandAndHandler[] = [];
        private currentlySending:boolean = false;
        private transmitter:Transmitter;
        private codec:cod.dolphin.Codec;
        clientDolphin:cd.dolphin.ClientDolphin; // todo: initialize via constructor


        constructor(transmitter:Transmitter) {
            this.transmitter = transmitter;
            this.codec = new cod.dolphin.Codec();
        }

        send(command:cmd.dolphin.Command, onFinished:OnFinishedAdapter) {
            this.commandQueue.push({command: command, handler: onFinished });
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
            this.transmitter.transmit([cmdAndHandler.command], (result:cmd.dolphin.Command[]) => {
                console.log("in onDone ")

                // handle the result

                this.doSendNext();  // recursive call: fetch the next in line
            });
        }

//        handle(serverCommand: amdcc.dolphin.AttributeMetadataChangedCommand): cpm.dolphin.ClientPresentationModel{
//            var clientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
//            if(!clientAttribute) return null;
//            //todo: implementation is remaining
//            return null;
//        }
//        handle(serverCommand: nca.dolphin.CallNamedActionCommand): cpm.dolphin.ClientPresentationModel{
//            this.clientDolphin.send(serverCommand.actionName,null);
//            return null;
//        }


    }
}