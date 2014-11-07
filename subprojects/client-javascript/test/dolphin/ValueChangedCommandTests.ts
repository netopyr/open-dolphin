import tsUnit = require("../../testsuite/tsUnit")
import valueChangedCmd     = require("../../js/dolphin/ValueChangedCommand")


export module dolphin {
    export class ValueChangedCommandTests extends tsUnit.tsUnit.TestClass {

        createValueChangedCommandWithGivenParameter(){
            var valueChangedCommand = new valueChangedCmd.dolphin.ValueChangedCommand("10", 10, 20);
            this.areIdentical(valueChangedCommand.id,"ValueChanged");
            this.areIdentical(valueChangedCommand.className,"org.opendolphin.core.comm.ValueChangedCommand");
            this.areIdentical(valueChangedCommand.attributeId, "10");
            this.areIdentical(valueChangedCommand.oldValue,10);
            this.areIdentical(valueChangedCommand.newValue,20);
        }

    }
}