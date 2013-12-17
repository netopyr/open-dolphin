var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var AttributeMetadataChangedCommand = (function (_super) {
            __extends(AttributeMetadataChangedCommand, _super);
            function AttributeMetadataChangedCommand(attributeId, metadataName, value) {
                _super.call(this);
                this.attributeId = attributeId;
                this.metadataName = metadataName;
                this.value = value;
                this.id = 'AttributeMetadataChanged';
                this.className = "org.opendolphin.core.comm.AttributeMetadataChangedCommand";
            }
            return AttributeMetadataChangedCommand;
        })(cmd.dolphin.Command);
        dolphin.AttributeMetadataChangedCommand = AttributeMetadataChangedCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=AttributeMetadataChangedCommand.js.map
