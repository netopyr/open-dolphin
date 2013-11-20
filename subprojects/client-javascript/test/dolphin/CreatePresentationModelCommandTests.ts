import tsUnit = require("../../testsuite/tsUnit")
import ca     = require("../../js/dolphin/ClientAttribute")
import cpm    = require("../../js/dolphin/ClientPresentationModel")
import createPMCmd     = require("../../js/dolphin/CreatePresentationModelCommand")


export module dolphin {
    export class CreatePresentationModelCommandTests extends tsUnit.tsUnit.TestClass {

        CreatePresentationModelCommandWithGivenParameter(){
            var pm = new cpm.dolphin.ClientPresentationModel("MyId","MyType");
            var clientAttribute1 =new ca.dolphin.ClientAttribute("prop1","qual1");
            var clientAttribute2 =new ca.dolphin.ClientAttribute("prop2","qual2");
            pm.addAttribute(clientAttribute1);
            pm.addAttribute(clientAttribute2);
            var createPMCommand = new createPMCmd.dolphin.CreatePresentationModelCommand(pm);
            this.areIdentical(createPMCommand.id,"CreatePresentationModel");
            this.areIdentical(createPMCommand.className,"org.opendolphin.core.comm.CreatePresentationModelCommand");
            this.areIdentical(createPMCommand.pmId,"MyId");
            this.areIdentical(createPMCommand.pmType,"MyType");

            this.areIdentical(createPMCommand.attributes.length,2);
            this.areIdentical(createPMCommand.attributes[0].propertyName,"prop1");
            this.areIdentical(createPMCommand.attributes[1].propertyName,"prop2");
        }

    }
}