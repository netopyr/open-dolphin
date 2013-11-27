import tsUnit = require("../../testsuite/tsUnit")
import cpm    = require("../../js/dolphin/ClientPresentationModel")
import map    = require("../../js/dolphin/Map")
import cd               = require("../../js/dolphin/ClientDolphin")
import cms    = require("../../js/dolphin/ClientModelStore")

export module dolphin {
    export class ClientModelStoreTests extends tsUnit.tsUnit.TestClass {

        addPresentationModelByType() {
            var pm1 = new cpm.dolphin.ClientPresentationModel("id1", "type");
            var pm2 = new cpm.dolphin.ClientPresentationModel("id2", "type");

            var clientDolphin = new cd.dolphin.ClientDolphin();
            var clientModelStore = new cms.dolphin.ClientModelStore(clientDolphin);

            clientModelStore.addPresentationModelByType(pm1);
            var pms:cpm.dolphin.ClientPresentationModel[] = clientModelStore.findAllPresentationModelByType(pm1.presentationModelType);

            this.areIdentical(pms.length, 1);
            this.areIdentical(pms[0].id, "id1");

            clientModelStore.addPresentationModelByType(pm2);
            this.areIdentical(pms.length, 2);
            this.areIdentical(pms[0].id, "id1");
            this.areIdentical(pms[1].id, "id2");

            clientModelStore.removePresentationModelByType(pm1);
            this.areIdentical(pms.length, 1);
            this.areIdentical(pms[0].id, "id2");
        }

    }
}