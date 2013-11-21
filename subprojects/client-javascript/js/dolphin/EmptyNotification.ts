import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class EmptyNotification extends cmd.dolphin.Command{

        className:string;
        constructor(){
            super();
            this.id = "Empty";
            this.className ="org.opendolphin.core.comm.EmptyNotification";
        }

    }
}