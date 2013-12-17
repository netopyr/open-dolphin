import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class DeletePresentationModelCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public pmId:string) {
            super();
            this.id = 'DeletePresentationModel';
            this.className = "org.opendolphin.core.comm.DeletePresentationModelCommand";
        }
    }
}