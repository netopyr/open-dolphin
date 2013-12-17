import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class GetPresentationModelCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public pmId:string) {
            super();
            this.id = 'GetPresentationModel';
            this.className = "org.opendolphin.core.comm.GetPresentationModelCommand";
        }
    }
}