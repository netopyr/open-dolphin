import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class PresentationModelResetedCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public pmId:string) {
            super();
            this.id = 'PresentationModelReseted';
            this.className = "org.opendolphin.core.comm.PresentationModelResetedCommand";
        }
    }
}