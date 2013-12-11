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

        checkPresentationModelIsDirty(){
            var pm = new cpm.dolphin.ClientPresentationModel(undefined,undefined);
            var ca1 = new ca.dolphin.ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ca.dolphin.ClientAttribute("prop2","qual2","value2","VALUE");

            pm.addAttribute(ca1);
            pm.addAttribute(ca2);
            // attributes are not dirty
            this.areIdentical(ca1.isDirty(),false);
            this.areIdentical(ca2.isDirty(),false);
            //PM is not dirty
            this.areIdentical(pm.isDirty(),false);
            // attribute1 is dirty
            ca1.setValue("anotherValue");
            this.areIdentical(ca1.isDirty(),true);

            //PM should be dirty
            this.areIdentical(pm.isDirty(),true);
        }

        checkPresentationModelIsDirtyAfterRebase(){
            var pm = new cpm.dolphin.ClientPresentationModel(undefined,undefined);
            var ca1 = new ca.dolphin.ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ca.dolphin.ClientAttribute("prop2","qual2","value2","VALUE");

            pm.addAttribute(ca1);
            pm.addAttribute(ca2);
            // attribute1 is dirty
            ca1.setValue("anotherValue");
            this.areIdentical(ca1.isDirty(),true);

            //PM should be dirty
            this.areIdentical(pm.isDirty(),true);

            pm.rebase();
            // attributes should not be dirty
            this.areIdentical(ca1.isDirty(),false);
            this.areIdentical(ca2.isDirty(),false);
            //PM should not be dirty
            this.areIdentical(pm.isDirty(),false);
        }

        checkPresentationModelIsDirtyAfterReset(){
            var pm = new cpm.dolphin.ClientPresentationModel(undefined,undefined);
            var ca1 = new ca.dolphin.ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ca.dolphin.ClientAttribute("prop2","qual2","value2","VALUE");

            pm.addAttribute(ca1);
            pm.addAttribute(ca2);

            // attribute1 is dirty
            ca1.setValue("anotherValue");
            this.areIdentical(ca1.isDirty(),true);

            //PM should be dirty
            this.areIdentical(pm.isDirty(),true);

            pm.reset();
            // attributes should not be dirty
            this.areIdentical(ca1.isDirty(),false);
            this.areIdentical(ca2.isDirty(),false);
            //PM should not be dirty
            this.areIdentical(pm.isDirty(),false);
        }
    }
}