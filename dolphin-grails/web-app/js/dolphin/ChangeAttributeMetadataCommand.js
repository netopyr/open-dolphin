var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var ChangeAttributeMetadataCommand = (function (_super) {
            __extends(ChangeAttributeMetadataCommand, _super);
            function ChangeAttributeMetadataCommand(attributeId, metadataName, value) {
                _super.call(this);
                this.attributeId = attributeId;
                this.metadataName = metadataName;
                this.value = value;
                this.id = 'ChangeAttributeMetadata';
                this.className = "org.opendolphin.core.comm.ChangeAttributeMetadataCommand";
            }
            return ChangeAttributeMetadataCommand;
        })(cmd.dolphin.Command);
        dolphin.ChangeAttributeMetadataCommand = ChangeAttributeMetadataCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=ChangeAttributeMetadataCommand.js.map
