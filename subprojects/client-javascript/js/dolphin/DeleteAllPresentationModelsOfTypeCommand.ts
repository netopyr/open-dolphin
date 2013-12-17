import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class DeleteAllPresentationModelsOfTypeCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public pmType:string) {
            super();
            this.id = 'DeleteAllPresentationModelsOfType';
            this.className = "org.opendolphin.core.comm.DeleteAllPresentationModelsOfTypeCommand";
        }
    }
}