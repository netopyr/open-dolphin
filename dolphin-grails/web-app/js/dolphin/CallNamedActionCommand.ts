import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class CallNamedActionCommand extends cmd.dolphin.Command {

        className:string;

        constructor(public actionName:string) {
            super();
            this.id = 'CallNamedAction';
            this.className = "org.opendolphin.core.comm.CallNamedActionCommand";
        }
    }
}