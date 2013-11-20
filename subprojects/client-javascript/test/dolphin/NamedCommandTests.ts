import tsUnit = require("../../testsuite/tsUnit")
import namedCmd     = require("../../js/dolphin/NamedCommand")


export module dolphin {
    export class NamedCommandTests extends tsUnit.tsUnit.TestClass {

        createNamedCommandWithGivenParameter(){
            var namedCommand = new namedCmd.dolphin.NamedCommand("CustomId");
            this.areIdentical(namedCommand.id,"CustomId");
            this.areIdentical(namedCommand.className,"org.opendolphin.core.comm.NamedCommand");
        }

    }
}