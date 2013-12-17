import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class SwitchPresentationModelCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public pmId:string, public sourcePmId:string) {
            super();
            this.id = 'SwitchPresentationModel';
            this.className = "org.opendolphin.core.comm.SwitchPresentationModelCommand";
        }
    }
}