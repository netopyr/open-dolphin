import cpm  = require("../../js/dolphin/ClientPresentationModel");
import ca   = require("../../js/dolphin/ClientAttribute");
import cmd  = require("../../js/dolphin/Command");
export module dolphin {

    export class CreatePresentationModelCommand extends cmd.dolphin.Command {

        pmId:string;
        className:string;
        pmType:string;
        attributes:ca.dolphin.ClientAttribute[] = [];

        constructor(presentationModel:cpm.dolphin.ClientPresentationModel) {
            super();
            this.id = "CreatePresentationModel";
            this.className = "org.opendolphin.core.comm.CreatePresentationModelCommand";
            this.pmId = presentationModel.id;
            this.pmType = presentationModel.presentationModelType;
            presentationModel.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                this.attributes.push(attribute);
            });
        }
    }
}