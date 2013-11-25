import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class SavedPresentationModelNotification extends cmd.dolphin.Command {

        className:string;

        constructor(public pmId:string) {
            super();
            this.id = 'SavedPresentationModel';
            this.className = "org.opendolphin.core.comm.SavedPresentationModelNotification";
        }
    }
}