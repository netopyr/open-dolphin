import tsUnit = require("../../testsuite/tsUnit")
import cpm    = require("../../js/dolphin/ClientPresentationModel")
import cc     = require("../../js/dolphin/ClientConnector")
import map    = require("../../js/dolphin/Map")
import cd     = require("../../js/dolphin/ClientDolphin")
import cms    = require("../../js/dolphin/ClientModelStore")
import ca     = require("../../js/dolphin/ClientAttribute")
import cmd    = require("../../js/dolphin/Command")

export module dolphin {

    class TestTransmitter implements cc.dolphin.Transmitter {
        constructor(public clientCommands, public serverCommands) {
        }

        transmit(commands:cmd.dolphin.Command[], onDone:(result:cmd.dolphin.Command[]) => void):void {
            this.clientCommands = commands;
            onDone(this.serverCommands);
        }
    }
    export class ClientModelStoreTests extends tsUnit.tsUnit.TestClass {

        addAndRemovePresentationModel() {
            var transmitter = new TestTransmitter(undefined, undefined)
            var clientConnector = new cc.dolphin.ClientConnector(transmitter);
            var clientDolphin = new cd.dolphin.ClientDolphin();
            clientDolphin.setClientConnector(clientConnector);
            var clientModelStore = new cms.dolphin.ClientModelStore(clientDolphin);

            var pm1 = new cpm.dolphin.ClientPresentationModel("id1", "type");
            var pm2 = new cpm.dolphin.ClientPresentationModel("id2", "type");
            clientModelStore.add(pm1);
            clientModelStore.add(pm2);

            var ids:string[] = clientModelStore.listPresentationModelIds();
            this.areIdentical(ids.length, 2);
            this.areIdentical(ids[1], "id2");

            var pms:cpm.dolphin.ClientPresentationModel[] = clientModelStore.listPresentationModels();
            this.areIdentical(pms.length, 2);
            this.areIdentical(pms[0], pm1);

            var pm = clientModelStore.findPresentationModelById("id2");
            this.areIdentical(pm, pm2);
            this.isTrue(clientModelStore.containsPresentationModel("id1"));

            clientModelStore.remove(pm1);
            var ids:string[] = clientModelStore.listPresentationModelIds();
            this.areIdentical(ids.length, 1);
            this.areIdentical(ids[0], "id2");

            var pms:cpm.dolphin.ClientPresentationModel[] = clientModelStore.listPresentationModels();
            this.areIdentical(pms.length, 1);
            this.areIdentical(pms[0], pm2);

            this.isFalse(clientModelStore.containsPresentationModel("id1"));
        }

        addAndRemovePresentationModelByType() {
            var pm1 = new cpm.dolphin.ClientPresentationModel("id1", "type");
            var pm2 = new cpm.dolphin.ClientPresentationModel("id2", "type");

            var clientDolphin = new cd.dolphin.ClientDolphin();
            var clientModelStore = new cms.dolphin.ClientModelStore(clientDolphin);

            clientModelStore.addPresentationModelByType(pm1);
            var pms:cpm.dolphin.ClientPresentationModel[] = clientModelStore.findAllPresentationModelByType(pm1.presentationModelType);

            this.areIdentical(pms.length, 1);
            this.areIdentical(pms[0].id, "id1");

            clientModelStore.addPresentationModelByType(pm2);
            var pms = clientModelStore.findAllPresentationModelByType(pm1.presentationModelType);
            this.areIdentical(pms.length, 2);
            this.areIdentical(pms[0].id, "id1");
            this.areIdentical(pms[1].id, "id2");

            clientModelStore.removePresentationModelByType(pm1);
            var pms = clientModelStore.findAllPresentationModelByType(pm1.presentationModelType);
            this.areIdentical(pms.length, 1);
            this.areIdentical(pms[0].id, "id2");
        }

        addAndRemoveAttributeById() {
            var clientDolphin = new cd.dolphin.ClientDolphin();
            var clientModelStore = new cms.dolphin.ClientModelStore(clientDolphin);

            var attr1 = new ca.dolphin.ClientAttribute("prop1", "qual1");
            var attr2 = new ca.dolphin.ClientAttribute("prop2", "qual2");

            clientModelStore.addAttributeById(attr1);
            clientModelStore.addAttributeById(attr2);

            var result1 = clientModelStore.findAttributeById(attr1.id);
            this.areIdentical(attr1, result1);

            var result2 = clientModelStore.findAttributeById(attr2.id);
            this.areIdentical(attr2, result2);

            clientModelStore.removeAttributeById(attr1);
            var result1 = clientModelStore.findAttributeById(attr1.id);
            this.areIdentical(result1, undefined);

        }

        addAndRemoveClientAttributeByQualifier() {
            var attr1 = new ca.dolphin.ClientAttribute("prop1", "qual1");
            var attr2 = new ca.dolphin.ClientAttribute("prop2", "qual2");

            var attr3 = new ca.dolphin.ClientAttribute("prop3", "qual1");
            var attr4 = new ca.dolphin.ClientAttribute("prop4", "qual2");

            var clientDolphin = new cd.dolphin.ClientDolphin();
            var clientModelStore = new cms.dolphin.ClientModelStore(clientDolphin);

            clientModelStore.addAttributeByQualifier(attr1);
            clientModelStore.addAttributeByQualifier(attr2);
            clientModelStore.addAttributeByQualifier(attr3);
            clientModelStore.addAttributeByQualifier(attr4);

            var clientAttrs1:ca.dolphin.ClientAttribute[] = clientModelStore.findAllAttributeByQualifier("qual1");

            this.areIdentical(clientAttrs1.length, 2);
            this.areIdentical(clientAttrs1[0].qualifier, "qual1");
            this.areIdentical(clientAttrs1[1].qualifier, "qual1");

            var clientAttrs2:ca.dolphin.ClientAttribute[] = clientModelStore.findAllAttributeByQualifier("qual2");

            this.areIdentical(clientAttrs2.length, 2);
            this.areIdentical(clientAttrs2[0].qualifier, "qual2");
            this.areIdentical(clientAttrs2[1].qualifier, "qual2");

            clientModelStore.removeAttributeByQualifier(attr1);
            var clientAttrs1:ca.dolphin.ClientAttribute[] = clientModelStore.findAllAttributeByQualifier("qual1");
            this.areIdentical(clientAttrs1.length, 1);
            this.areIdentical(clientAttrs1[0].qualifier, "qual1");
            this.areIdentical(clientAttrs1[1], undefined);


        }

    }
}