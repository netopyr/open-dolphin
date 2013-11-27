import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class DeleteAllPresentationModelsOfTypeNotification extends cmd.dolphin.Command {

        className:string;

        constructor(public pmType:string) {
            super();
            this.id = 'DeleteAllPresentationModels';
            this.className = "org.opendolphin.core.comm.DeleteAllPresentationModelsOfTypeNotification";
        }
    }
}