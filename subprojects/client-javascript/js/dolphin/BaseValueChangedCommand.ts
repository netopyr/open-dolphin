import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class BaseValueChangedCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public attributeId:number) {
            super();
            this.id = 'BaseValueChanged';
            this.className = "org.opendolphin.core.comm.BaseValueChangedCommand";
        }
    }
}