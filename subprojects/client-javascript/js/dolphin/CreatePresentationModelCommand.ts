import cpm  = require("../../js/dolphin/ClientPresentationModel");
import ca   = require("../../js/dolphin/ClientAttribute");
import cmd  = require("../../js/dolphin/Command");
export module dolphin {

    export class CreatePresentationModelCommand extends cmd.dolphin.Command {

        pmId:string;
        className:string;
        pmType:string;
        attributes:any[] = [];
        clientSideOnly:boolean = false;

        constructor(presentationModel:cpm.dolphin.ClientPresentationModel) {
            super();
            this.id = "CreatePresentationModel";
            this.className = "org.opendolphin.core.comm.CreatePresentationModelCommand";
            this.pmId = presentationModel.id;
            this.pmType = presentationModel.presentationModelType;

            var attrs = this.attributes
            presentationModel.getAttributes().forEach(function(attr) {
                attrs.push({
                    propertyName:   attr.propertyName,
                    id:             attr.id,
                    qualifier:      attr.getQualifier(),
                    value:          attr.getValue(),
                    tag:            attr.tag
                });
            });

        }
    }
}