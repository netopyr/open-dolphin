import tsUnit           = require("../../testsuite/tsUnit")
import cod              = require("../../js/dolphin/Codec")
import ca               = require("../../js/dolphin/ClientAttribute")
import cpm              = require("../../js/dolphin/ClientPresentationModel")
import createPMCmd      = require("../../js/dolphin/CreatePresentationModelCommand")
import mdcCmd           = require("../../js/dolphin/AttributeMetadataChangedCommand")
import callNameCmd      = require("../../js/dolphin/CallNamedActionCommand")
import changeAttrCmd    = require("../../js/dolphin/ChangeAttributeMetadataCommand")
import attrCreatedCmd   = require("../../js/dolphin/AttributeCreatedNotification")
import getPMCmd         = require("../../js/dolphin/GetPresentationModelCommand")
import delAllPMCmd      = require("../../js/dolphin/DeleteAllPresentationModelsOfTypeCommand")
import delAllPMTypeNot  = require("../../js/dolphin/DeletedAllPresentationModelsOfTypeNotification")
import delPMNot         = require("../../js/dolphin/DeletedPresentationModelNotification")
import delPMCmd         = require("../../js/dolphin/DeletePresentationModelCommand")
import emptyNot         = require("../../js/dolphin/EmptyNotification")
import initAttr         = require("../../js/dolphin/InitializeAttributeCommand")
import bvcCmd           = require("../../js/dolphin/BaseValueChangedCommand")
import nmdCmd           = require("../../js/dolphin/NamedCommand")
import pmReCmd          = require("../../js/dolphin/PresentationModelResetedCommand")
import rsPmCmd          = require("../../js/dolphin/ResetPresentationModelCommand")
import savePmNot        = require("../../js/dolphin/SavedPresentationModelNotification")
import switchPmCmd      = require("../../js/dolphin/SwitchPresentationModelCommand")
import valChngCmd       = require("../../js/dolphin/ValueChangedCommand")
import dataCmd          = require("../../js/dolphin/DataCommand")

export module dolphin {
    export class CodecTest extends tsUnit.tsUnit.TestClass {

        testCodingCreatePresentationModel() {
            var pm = new cpm.dolphin.ClientPresentationModel("MyId", "MyType");
            var clientAttribute1 = new ca.dolphin.ClientAttribute("prop1", "qual1", 0);
            var clientAttribute2 = new ca.dolphin.ClientAttribute("prop2", "qual2", 0);
            pm.addAttribute(clientAttribute1);
            pm.addAttribute(clientAttribute2);
            var createPMCommand = new createPMCmd.dolphin.CreatePresentationModelCommand(pm);

            var codec = new cod.dolphin.Codec();

            var coded = codec.encode(createPMCommand);
            var decoded = codec.decode(coded);

            this.isTrue(createPMCommand.toString() === decoded.toString());
        }

        testEmpty() {
            this.isTrue(CodecTestHelper.testSoManyCommandsEncoding(0));
        }

        testOne() {
            this.isTrue(CodecTestHelper.testSoManyCommandsEncoding(1));
        }

        testMany() {
            this.isTrue(CodecTestHelper.testSoManyCommandsEncoding(10));
        }

        testCodingCommands() {
            this.isTrue(CodecTestHelper.testCodingCommand(new attrCreatedCmd.dolphin.AttributeCreatedNotification("pmId", 5, "prop", "value", "qualifier", "TOOLTIP")))
            this.isTrue(CodecTestHelper.testCodingCommand(new mdcCmd.dolphin.AttributeMetadataChangedCommand(5, "name", "value")))
            this.isTrue(CodecTestHelper.testCodingCommand(new callNameCmd.dolphin.CallNamedActionCommand("some-action")))
            this.isTrue(CodecTestHelper.testCodingCommand(new createPMCmd.dolphin.CreatePresentationModelCommand(new cpm.dolphin.ClientPresentationModel("MyId", "MyType"))))
            this.isTrue(CodecTestHelper.testCodingCommand(new changeAttrCmd.dolphin.ChangeAttributeMetadataCommand(5, "name", "value")))
            this.isTrue(CodecTestHelper.testCodingCommand(new getPMCmd.dolphin.GetPresentationModelCommand("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new dataCmd.dolphin.DataCommand("test")));
            this.isTrue(CodecTestHelper.testCodingCommand(new delAllPMCmd.dolphin.DeleteAllPresentationModelsOfTypeCommand("type")))
            this.isTrue(CodecTestHelper.testCodingCommand(new delAllPMTypeNot.dolphin.DeletedAllPresentationModelsOfTypeNotification("type")))
            this.isTrue(CodecTestHelper.testCodingCommand(new delPMNot.dolphin.DeletedPresentationModelNotification("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new delPMCmd.dolphin.DeletePresentationModelCommand("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new emptyNot.dolphin.EmptyNotification()))
            this.isTrue(CodecTestHelper.testCodingCommand(new initAttr.dolphin.InitializeAttributeCommand("pmId", "prop", "qualifier", "value", "pmType")))
            this.isTrue(CodecTestHelper.testCodingCommand(new bvcCmd.dolphin.BaseValueChangedCommand(5)))
            this.isTrue(CodecTestHelper.testCodingCommand(new nmdCmd.dolphin.NamedCommand("name")))
            this.isTrue(CodecTestHelper.testCodingCommand(new pmReCmd.dolphin.PresentationModelResetedCommand("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new rsPmCmd.dolphin.ResetPresentationModelCommand("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new savePmNot.dolphin.SavedPresentationModelNotification("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new switchPmCmd.dolphin.SwitchPresentationModelCommand("pmId", "sourcePmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new valChngCmd.dolphin.ValueChangedCommand(5, "oldValue", "newValue")))
        }
    }

    class CodecTestHelper {

        static testSoManyCommandsEncoding(count:number):boolean {
            var codec:cod.dolphin.Codec = new cod.dolphin.Codec();
            var commands:attrCreatedCmd.dolphin.AttributeCreatedNotification[] = [];

            for (var i = 0; i < count; i++) {
                commands.push(new attrCreatedCmd.dolphin.AttributeCreatedNotification(i.toString(), i * count, "prop" + i, "value" + i, null));
            }

            var coded = codec.encode(commands);
            var decoded = codec.decode(coded);

            if (commands.toString() === decoded.toString()) {
                return true;
            } else {
                return false;
            }
        }

        static testCodingCommand(command:any) {
            var codec:cod.dolphin.Codec = new cod.dolphin.Codec();
            var coded = codec.encode(command);
            var decoded = codec.decode(coded);
            if (command.toString() === decoded.toString()) {
                return true;
            } else {
                return false;
            }
        }
    }
}