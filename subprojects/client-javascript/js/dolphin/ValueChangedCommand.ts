import cmd = require("../../js/dolphin/Command");
export module dolphin {


    export class ValueChangedCommand extends cmd.dolphin.Command{

        className:string;

        constructor(public attributeId:number, public oldValue:any, public newValue:any) {
            super();
            this.id = "ValueChanged";
            this.className ="org.opendolphin.core.comm.ValueChangedCommand";
        }

    }

}