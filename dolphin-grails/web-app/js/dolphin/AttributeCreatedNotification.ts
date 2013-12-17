import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class AttributeCreatedNotification extends cmd.dolphin.Command {

        className:string;

        constructor(public pmId:string, public attributeId:number, public propertyName:string, public newValue:any, public qualifier:string, public tag:string = "VALUE") {
            super();
            this.id = 'AttributeCreated';
            this.className = "org.opendolphin.core.comm.AttributeCreatedNotification";
        }
    }
}