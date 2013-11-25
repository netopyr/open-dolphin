import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class ResetPresentationModelCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public pmId:string) {
            super();
            this.id = 'ResetPresentationModel';
            this.className = "org.opendolphin.core.comm.ResetPresentationModelCommand";
        }
    }
}