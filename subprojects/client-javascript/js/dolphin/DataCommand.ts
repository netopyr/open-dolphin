import cmd = require("../../js/dolphin/Command");
export module dolphin {


    export class DataCommand extends cmd.dolphin.Command{

        className:string;

        constructor(public data:any) {
            super();
            this.id = "Data";
            this.className ="org.opendolphin.core.comm.DataCommand";
        }

    }

}