import tsUnit = require("../../testsuite/tsUnit")
import ca     = require("../../js/dolphin/ClientAttribute")
import cpm    = require("../../js/dolphin/ClientPresentationModel")
import cd     = require("../../js/dolphin/ClientDolphin")
import cms    = require("../../js/dolphin/ClientModelStore")
import cc     = require("../../js/dolphin/ClientConnector")
import cmd    = require("../../js/dolphin/Command")


export module dolphin {
    export class ClientDolphinTests extends tsUnit.tsUnit.TestClass {

        getPmFromFactoryMethod() {
            var clientDolphin:cd.dolphin.ClientDolphin = new cd.dolphin.ClientDolphin();
            var clientModelStore:cms.dolphin.ClientModelStore = new cms.dolphin.ClientModelStore(clientDolphin);
            clientDolphin.setClientModelStore(clientModelStore);
            clientDolphin.setClientConnector(new cc.dolphin.ClientConnector({transmit: (result:cmd.dolphin.Command[]) => {
            } },clientDolphin));

            var pm1:cpm.dolphin.ClientPresentationModel = clientDolphin.presentationModel("myId1", "myType");
            this.areIdentical(pm1.id, "myId1");
            this.areIdentical(pm1.getAttributes().length, 0);

            var ca1 = new ca.dolphin.ClientAttribute("prop1", "qual1", "val");
            var ca2 = new ca.dolphin.ClientAttribute("prop2", "qual2", "val");

            var pm2:cpm.dolphin.ClientPresentationModel = clientDolphin.presentationModel("myId2", "myType", ca1, ca2);
            this.areIdentical(pm2.id, "myId2");
            this.areIdentical(pm2.getAttributes().length, 2);
        }

    }
}