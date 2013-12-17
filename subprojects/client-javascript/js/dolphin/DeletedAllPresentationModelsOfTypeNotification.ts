import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class DeletedAllPresentationModelsOfTypeNotification extends cmd.dolphin.Command {

        className:string;

        constructor(public pmType:string) {
            super();
            this.id = 'DeletedAllPresentationModelsOfType';
            this.className = "org.opendolphin.core.comm.DeletedAllPresentationModelsOfTypeNotification";
        }
    }
}