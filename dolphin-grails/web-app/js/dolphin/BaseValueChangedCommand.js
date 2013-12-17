var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var BaseValueChangedCommand = (function (_super) {
            __extends(BaseValueChangedCommand, _super);
            function BaseValueChangedCommand(attributeId) {
                _super.call(this);
                this.attributeId = attributeId;
                this.id = 'BaseValueChanged';
                this.className = "org.opendolphin.core.comm.BaseValueChangedCommand";
            }
            return BaseValueChangedCommand;
        })(cmd.dolphin.Command);
        dolphin.BaseValueChangedCommand = BaseValueChangedCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=BaseValueChangedCommand.js.map
