import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class ChangeAttributeMetadataCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public attributeId:number, public metadataName:string, public value:any) {
            super();
            this.id = 'ChangeAttributeMetadata';
            this.className = "org.opendolphin.core.comm.ChangeAttributeMetadataCommand";
        }
    }
}