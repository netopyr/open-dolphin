import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class AttributeMetadataChangedCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public attributeId:number, public metadataName:string, public value:any) {
            super();
            this.id = 'AttributeMetadataChanged';
            this.className = "org.opendolphin.core.comm.AttributeMetadataChangedCommand";
        }
    }
}