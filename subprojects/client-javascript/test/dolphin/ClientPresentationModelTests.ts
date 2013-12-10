import tsUnit = require("../../testsuite/tsUnit")
import ca     = require("../../js/dolphin/ClientAttribute")
import cpm    = require("../../js/dolphin/ClientPresentationModel")


export module dolphin {
    export class ClientPresentationModelTests extends tsUnit.tsUnit.TestClass {

        createPmWithAutomaticId() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined,undefined);
            var pm2 = new cpm.dolphin.ClientPresentationModel(undefined,undefined);
            this.areNotIdentical(pm1.id, pm2.id);
        }

        createPmWithGivenId() {
            var pm1 = new cpm.dolphin.ClientPresentationModel("MyId",undefined);
            this.areIdentical(pm1.id, "MyId");
        }

        createPmWithGivenType() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined,"MyType");
            this.areIdentical(pm1.presentationModelType, "MyType");
        }

        addingClientAttributes() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined,undefined);
            this.areIdentical(pm1.attributes.length, 0);
            var firstAttribute = new ca.dolphin.ClientAttribute("prop", "qual", 0);
            pm1.addAttribute(firstAttribute);
            this.areIdentical(pm1.attributes.length, 1);
            this.areIdentical(pm1.attributes[0], firstAttribute);
        }

        invalidateClientPresentationModelEvent(){
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined,undefined);
            var clientAttribute = new ca.dolphin.ClientAttribute("prop", "qual", 0);
            pm1.addAttribute(clientAttribute);
            var source;
            pm1.onInvalidated((event:cpm.dolphin.InvalidationEvent) => {
                source=event.source;
            });
            clientAttribute.setValue("newValue");
            this.areIdentical(pm1,source);
        }
    }
}