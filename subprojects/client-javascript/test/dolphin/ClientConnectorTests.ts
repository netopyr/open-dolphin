import tsUnit = require("../../testsuite/tsUnit")
import cc     = require("../../js/dolphin/ClientConnector")
import hcc    = require("../../js/dolphin/HttpTransmitter")
import cmd    = require("../../js/dolphin/Command")
import cd     = require("../../js/dolphin/ClientDolphin")
import cms    = require("../../js/dolphin/ClientModelStore")
import cpm    = require("../../js/dolphin/ClientPresentationModel")
import ca     = require("../../js/dolphin/ClientAttribute")
import cna    = require("../../js/dolphin/CallNamedActionCommand");
import amdcc  = require("../../js/dolphin/AttributeMetadataChangedCommand");
import pmrc   = require("../../js/dolphin/PresentationModelResetedCommand");
import spmn   = require("../../js/dolphin/SavedPresentationModelNotification");
import iac    = require("../../js/dolphin/InitializeAttributeCommand");
import spmc   = require("../../js/dolphin/SwitchPresentationModelCommand");
import bvcc   = require("../../js/dolphin/BaseValueChangedCommand");
import vcc    = require("../../js/dolphin/ValueChangedCommand");
import dapm   = require("../../js/dolphin/DeleteAllPresentationModelsOfTypeCommand");
import dapmc  = require("../../js/dolphin/DeleteAllPresentationModelsOfTypeCommand");
import dpmc   = require("../../js/dolphin/DeletePresentationModelCommand");
import cpmc   = require("../../js/dolphin/CreatePresentationModelCommand");


export module dolphin {

    class TestTransmitter implements cc.dolphin.Transmitter {
        constructor(public clientCommands, public serverCommands) {
        }

        transmit(commands:cmd.dolphin.Command[], onDone: (result: cmd.dolphin.Command[]) => void ) : void {
            this.clientCommands = commands;
            onDone(this.serverCommands);
        }
    }

    export class ClientConnectorTests extends tsUnit.tsUnit.TestClass {

        sendingOneCommandMustCallTheTransmission() {
            var singleCommand   = new cmd.dolphin.Command();
            var serverCommand:cmd.dolphin.Command[]=[];
            var transmitter     = new TestTransmitter(singleCommand, serverCommand)
            var clientConnector = new cc.dolphin.ClientConnector(transmitter,null);

            clientConnector.send(singleCommand, undefined)

            this.areIdentical( transmitter.clientCommands.length, 1)
            this.areIdentical( transmitter.clientCommands[0], singleCommand)
        }

        sendingMultipleCommands() {
            var singleCommand   = new cmd.dolphin.Command();
            var serverCommand:cmd.dolphin.Command[]=[];
            var lastCommand     = new cmd.dolphin.Command();
            var transmitter     = new TestTransmitter(undefined, serverCommand)
            var clientConnector = new cc.dolphin.ClientConnector(transmitter,null);

            clientConnector.send(singleCommand, undefined)
            clientConnector.send(singleCommand, undefined)
            clientConnector.send(lastCommand, undefined)

            this.areIdentical( transmitter.clientCommands.length, 1)
            this.areIdentical( transmitter.clientCommands[0], lastCommand)
        }

        handleDeletePresentationModelCommand(){
            TestHelper.initialize();
            var serverCommand:dpmc.dolphin.DeletePresentationModelCommand = new dpmc.dolphin.DeletePresentationModelCommand("pmId1")

            //before calling DeletePresentationModelCommand
            var pm1 = TestHelper.clientDolphin.findPresentationModelById("pmId1");
            this.areIdentical(pm1.id,"pmId1");

            //call DeletePresentationModelCommand
            TestHelper.clientConnector.handle(serverCommand);
            pm1 = TestHelper.clientDolphin.findPresentationModelById("pmId1");
            this.areIdentical(pm1,undefined); // should be undefined

            //other PM should be unaffected
            var pm2 = TestHelper.clientDolphin.findPresentationModelById("pmId2");
            this.areIdentical(pm2.id,"pmId2");

            //deleting with dummyId
            serverCommand  = new dpmc.dolphin.DeletePresentationModelCommand("dummyId")
            var result = TestHelper.clientConnector.handle(serverCommand);
            this.areIdentical(result,null);// there is no pm with dummyId
        }

        handleDeleteAllPresentationModelOfTypeCommand(){
            TestHelper.initialize();
            var serverCommand:dapmc.dolphin.DeleteAllPresentationModelsOfTypeCommand = new dapmc.dolphin.DeleteAllPresentationModelsOfTypeCommand("pmType")

            //before calling DeleteAllPresentationModelsOfTypeCommand
            var pms = TestHelper.clientDolphin.findAllPresentationModelByType("pmType");
            this.areIdentical(pms.length,2);

            //call DeleteAllPresentationModelsOfTypeCommand
            TestHelper.clientConnector.handle(serverCommand);
            pms = TestHelper.clientDolphin.findAllPresentationModelByType("pmType");
            this.areIdentical(pms.length,0); //both pm of pmType is deleted

            //initialize again
            TestHelper.initialize();
            //sending dummyType
            serverCommand = new dapmc.dolphin.DeleteAllPresentationModelsOfTypeCommand("dummyType")
            TestHelper.clientConnector.handle(serverCommand);
            var pms = TestHelper.clientDolphin.findAllPresentationModelByType("pmType");
            this.areIdentical(pms.length,2);// nothing is deleted
        }

        handleValueChangedCommand(){
            TestHelper.initialize();
            var serverCommand:vcc.dolphin.ValueChangedCommand = new vcc.dolphin.ValueChangedCommand(TestHelper.attr1.id,0,10);

            //before calling ValueChangedCommand
            var attribute = TestHelper.clientDolphin.getClientModelStore().findAttributeById(TestHelper.attr1.id);
            this.areIdentical(attribute.getValue, TestHelper.attr1.getValue);
            this.areIdentical(attribute.getValue(),0);

            //call ValueChangedCommand
            TestHelper.clientConnector.handle(serverCommand);
            attribute = TestHelper.clientDolphin.getClientModelStore().findAttributeById(TestHelper.attr1.id);
            this.areIdentical(attribute.getValue(), TestHelper.attr1.getValue());
            this.areIdentical(attribute.getValue(),10);
        }

        handleBaseValueChangedCommand(){
            TestHelper.initialize();
            var serverCommand:bvcc.dolphin.BaseValueChangedCommand = new bvcc.dolphin.BaseValueChangedCommand(TestHelper.attr1.id);

            //before calling ValueChangedCommand
            var attribute = TestHelper.clientDolphin.getClientModelStore().findAttributeById(TestHelper.attr1.id);
            this.areIdentical(attribute.getBaseValue(), TestHelper.attr1.getBaseValue());
            this.areIdentical(attribute.getBaseValue(),0);

            TestHelper.attr1.setValue(10); //change value
            //call BaseValueChangedCommand
            TestHelper.clientConnector.handle(serverCommand);
            attribute = TestHelper.clientDolphin.getClientModelStore().findAttributeById(TestHelper.attr1.id);
            this.areIdentical(attribute.getBaseValue(), TestHelper.attr1.getBaseValue());
            this.areIdentical(attribute.getBaseValue(),10);
        }

        handleSwitchPresentationModelCommand(){
            TestHelper.initialize();
            var serverCommand:spmc.dolphin.SwitchPresentationModelCommand = new spmc.dolphin.SwitchPresentationModelCommand("pmId1","pmId2");

            //before calling SwitchPresentationModelCommand
            var pms = TestHelper.clientDolphin.findAllPresentationModelByType("pmType");
            this.areNotIdentical(pms[0].getAttributes()[0].getValue(), pms[1].getAttributes()[0].getValue());
            this.areNotIdentical(pms[0].getAttributes()[0].getBaseValue(), pms[1].getAttributes()[0].getBaseValue());

            //call SwitchPresentationModelCommand
            TestHelper.clientConnector.handle(serverCommand);
            pms = TestHelper.clientDolphin.findAllPresentationModelByType("pmType");
            // Both attribute of same property and tag ("prop1", "VALUE")  should be equal
            this.areIdentical(pms[0].getAttributes()[0].getValue(), pms[1].getAttributes()[0].getValue());
            this.areIdentical(pms[0].getAttributes()[0].getBaseValue(), pms[1].getAttributes()[0].getBaseValue());

            //other attributes should be unaffected
            this.areNotIdentical(pms[0].getAttributes()[1].getValue(), pms[1].getAttributes()[1].getValue());
            this.areNotIdentical(pms[0].getAttributes()[1].getBaseValue(), pms[1].getAttributes()[1].getBaseValue());
        }

        handleSavedPresentationModelNotification(){
            TestHelper.initialize();
            var serverCommand:spmn.dolphin.SavedPresentationModelNotification = new spmn.dolphin.SavedPresentationModelNotification("pmId1");

            //before calling SavedPresentationModelNotification
            var pm = TestHelper.clientDolphin.findPresentationModelById("pmId1");
            this.areIdentical(pm.getAttributes()[0].getValue(), pm.getAttributes()[0].getBaseValue());
            this.areIdentical(pm.getAttributes()[1].getValue(), pm.getAttributes()[1].getBaseValue());

            TestHelper.attr1.setValue(10);
            //call SavedPresentationModelNotification
            TestHelper.clientConnector.handle(serverCommand);
            pm = TestHelper.clientDolphin.findPresentationModelById("pmId1");
            this.areIdentical(pm.getAttributes()[0].getValue(), pm.getAttributes()[0].getBaseValue());
            this.areIdentical(pm.getAttributes()[1].getValue(), pm.getAttributes()[1].getBaseValue());

            this.areIdentical(pm.getAttributes()[0].getValue(),10);
            this.areIdentical(pm.getAttributes()[1].getValue(), 0);
            this.areIdentical(pm.getAttributes()[0].getBaseValue(),10);
            this.areIdentical(pm.getAttributes()[1].getBaseValue(), 0);
        }
        handlePresentationModelResetedCommand(){
            TestHelper.initialize();
            var serverCommand: pmrc.dolphin.PresentationModelResetedCommand = new  pmrc.dolphin.PresentationModelResetedCommand("pmId1");

            //before calling PresentationModelResetedCommand
            var pm = TestHelper.clientDolphin.findPresentationModelById("pmId1");
            this.areIdentical(pm.getAttributes()[0].getValue(), pm.getAttributes()[0].getBaseValue());
            this.areIdentical(pm.getAttributes()[1].getValue(), pm.getAttributes()[1].getBaseValue());

            TestHelper.attr1.setValue(10);
            //call PresentationModelResetedCommand
            TestHelper.clientConnector.handle(serverCommand);
            pm = TestHelper.clientDolphin.findPresentationModelById("pmId1");
            this.areIdentical(pm.getAttributes()[0].getValue(), pm.getAttributes()[0].getBaseValue());
            this.areIdentical(pm.getAttributes()[1].getValue(), pm.getAttributes()[1].getBaseValue());

            this.areIdentical(pm.getAttributes()[0].getValue(),0);
            this.areIdentical(pm.getAttributes()[1].getValue(), 0);
            this.areIdentical(pm.getAttributes()[0].getBaseValue(),0);
            this.areIdentical(pm.getAttributes()[1].getBaseValue(), 0);
        }
        handleInitializeAttributeCommand(){
            TestHelper.initialize();
            //new PM with existing attribute qualifier
            var serverCommand: iac.dolphin.InitializeAttributeCommand = new  iac.dolphin.InitializeAttributeCommand("newPm","newPmType","newProp","qual1","newValue");
            //before calling InitializeAttributeCommand
            var attribute = TestHelper.clientDolphin.getClientModelStore().findAllAttributeByQualifier("qual1");
            this.areIdentical(attribute[0].getValue(), 0);
            this.areIdentical(TestHelper.clientDolphin.listPresentationModelIds().length, 2);

            //call InitializeAttributeCommand
            TestHelper.clientConnector.handle(serverCommand);
            attribute = TestHelper.clientDolphin.getClientModelStore().findAllAttributeByQualifier("qual1");
            this.areIdentical(attribute[0].getValue(), "newValue");// same attribute value will change
            this.areIdentical(TestHelper.clientDolphin.listPresentationModelIds().length, 3);

            //existing PM with existing attribute qualifier
            var serverCommand: iac.dolphin.InitializeAttributeCommand = new  iac.dolphin.InitializeAttributeCommand("pmId1","pmType1","newProp","qual3","newValue");
            //before calling InitializeAttributeCommand
            var attribute = TestHelper.clientDolphin.getClientModelStore().findAllAttributeByQualifier("qual3");
            this.areIdentical(attribute[0].getValue(), 5);
            this.areIdentical(TestHelper.clientDolphin.listPresentationModelIds().length, 3);

            //call InitializeAttributeCommand
            TestHelper.clientConnector.handle(serverCommand);
            attribute = TestHelper.clientDolphin.getClientModelStore().findAllAttributeByQualifier("qual3");
            this.areIdentical(attribute[0].getValue(), "newValue");// same attribute value will change
            this.areIdentical(TestHelper.clientDolphin.listPresentationModelIds().length, 3);// no PM added
        }

    }

    class TestHelper{
        static transmitter:TestTransmitter;
        static clientDolphin:cd.dolphin.ClientDolphin;
        static clientConnector:cc.dolphin.ClientConnector;
        static clientModelStore:cms.dolphin.ClientModelStore;
        static attr1:ca.dolphin.ClientAttribute;// to access for id
        static attr3:ca.dolphin.ClientAttribute;// to access for id

        static initialize(){
            var serverCommand:cmd.dolphin.Command[]=[];//to test
            this.transmitter = new TestTransmitter(undefined, serverCommand);
            this.clientDolphin = new cd.dolphin.ClientDolphin();
            this.clientConnector = new cc.dolphin.ClientConnector(this.transmitter,this.clientDolphin);
            this.clientModelStore = new cms.dolphin.ClientModelStore(this.clientDolphin);
            this.clientDolphin.setClientModelStore(this.clientModelStore);
            this.clientDolphin.setClientConnector(this.clientConnector);

            this.attr1 = new ca.dolphin.ClientAttribute("prop1", "qual1", 0);
            var attr2 = new ca.dolphin.ClientAttribute("prop2", "qual2", 0);

            this.attr3 = new ca.dolphin.ClientAttribute("prop1", "qual3", 5);
            var attr4 = new ca.dolphin.ClientAttribute("prop4", "qual4", 5);

            this.clientDolphin.presentationModel("pmId1", "pmType",this.attr1,attr2);
            this.clientDolphin.presentationModel("pmId2", "pmType",this.attr3,attr4);
        }
    }
}