import tsUnit = require("../../testsuite/tsUnit")
import changedAttrMDCmd     = require("../../js/dolphin/ChangeAttributeMetadataCommand")


export module dolphin {
    export class ChangeAttributeMetadataCommandTests extends tsUnit.tsUnit.TestClass {

        createChangedAttrMetaDataCommandWithGivenParameter(){
            var changedAttrMDCommand = new changedAttrMDCmd.dolphin.ChangeAttributeMetadataCommand(10, "MDName", 20);
            this.areIdentical(changedAttrMDCommand.id,"ChangeAttributeMetadata");
            this.areIdentical(changedAttrMDCommand.className,"org.opendolphin.core.comm.ChangeAttributeMetadataCommand");
            this.areIdentical(changedAttrMDCommand.attributeId, 10);
            this.areIdentical(changedAttrMDCommand.metadataName,"MDName");
            this.areIdentical(changedAttrMDCommand.value,20);

        }

    }
}