import cmd = require("../../js/dolphin/Command");
export module dolphin {


    export class DataCommand extends cmd.dolphin.Command{

        data:any;
        className:string;

        constructor() {
            super();
            this.id = "Data";
            this.className ="org.opendolphin.core.comm.DataCommand";
        }

    }

}