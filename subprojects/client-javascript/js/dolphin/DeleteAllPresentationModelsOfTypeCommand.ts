import cmd = require("../../js/dolphin/DeleteAllPresentationModelsOfTypeNotification");
export module dolphin {

    export class DeleteAllPresentationModelsOfTypeCommand extends cmd.dolphin.DeleteAllPresentationModelsOfTypeNotification {

        className:string;

        constructor(pmType:string) {
            super(pmType);
            this.id = 'DeleteAllPresentationModelsOfTypeCommand';
            this.className = "org.opendolphin.core.comm.DeleteAllPresentationModelsOfTypeCommand";
        }
    }
}