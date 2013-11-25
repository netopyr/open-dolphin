import cmd = require("../../js/dolphin/Command");
export module dolphin {

    export class NamedCommand extends cmd.dolphin.Command {

        className:string;

        constructor(name:string) {
            super();
            this.id = name;
            this.className = "org.opendolphin.core.comm.NamedCommand";
        }

    }

}
