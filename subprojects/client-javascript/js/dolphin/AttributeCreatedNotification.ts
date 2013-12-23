import cmd = require("../../js/dolphin/Command");
import tags = require("../../js/dolphin/Tag")
export module dolphin {

    export class AttributeCreatedNotification extends cmd.dolphin.Command {

        className:string;

        constructor(public pmId:string, public attributeId:number, public propertyName:string, public newValue:any, public qualifier:string, public tag:string = tags.dolphin.Tag.value()) {
            super();
            this.id = 'AttributeCreated';
            this.className = "org.opendolphin.core.comm.AttributeCreatedNotification";
        }
    }
}