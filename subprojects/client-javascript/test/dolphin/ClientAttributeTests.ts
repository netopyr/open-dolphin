import tsUnit = require("../../testsuite/tsUnit")
import ca     = require("../../js/dolphin/ClientAttribute")


export module dolphin {
    export class ClientAttributeTests extends tsUnit.tsUnit.TestClass {

        attributesShouldGetUniqueIds() {
            var ca1 = new ca.dolphin.ClientAttribute("prop","qual","tag");
            var ca2 = new ca.dolphin.ClientAttribute("prop","qual","tag");
            this.areNotIdentical(ca1.id, ca2.id);
        }

        valueListenersAreCalled() {
            var attr = new ca.dolphin.ClientAttribute("prop", "qual");

            attr.setValue(0);

            var spoofedOld = -1;
            var spoofedNew = -1;
            attr.onValueChange( (evt: ca.dolphin.ValueChangedEvent) => {
                spoofedOld = evt.oldValue;
                spoofedNew = evt.newValue;
            } )

            attr.setValue(1);

            this.areIdentical(spoofedOld, 0)
            this.areIdentical(spoofedNew, 1)

        }

        valueListenersDoNotInterfere() {
            var attr1 = new ca.dolphin.ClientAttribute("prop", "qual1");
            var attr2 = new ca.dolphin.ClientAttribute("prop", "qual2");

            var spoofedNew1 = -1;
            attr1.onValueChange( (evt: ca.dolphin.ValueChangedEvent) => {
                spoofedNew1 = evt.newValue;
            } )
            attr1.setValue(1);

            var spoofedNew2 = -1;
            attr2.onValueChange( (evt: ca.dolphin.ValueChangedEvent) => {
                spoofedNew2 = evt.newValue;
            } )
            attr2.setValue(2);

            this.areIdentical(spoofedNew1, 1)
            this.areIdentical(spoofedNew2, 2)

        }
    }
}