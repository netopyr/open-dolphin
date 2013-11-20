import tsUnit = require("../../testsuite/tsUnit")
import emptyN     = require("../../js/dolphin/EmptyNotification")


export module dolphin {
    export class EmptyNotificationTests extends tsUnit.tsUnit.TestClass {

        createEmptyNotificationWithGivenParameter(){
            var emptyNotification = new emptyN.dolphin.EmptyNotification();
            this.areIdentical(emptyNotification.id,"Empty");
            this.areIdentical(emptyNotification.className,"org.opendolphin.core.comm.EmptyNotification");
        }

    }
}