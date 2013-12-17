var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var ValueChangedCommand = (function (_super) {
            __extends(ValueChangedCommand, _super);
            function ValueChangedCommand(attributeId, oldValue, newValue) {
                _super.call(this);
                this.attributeId = attributeId;
                this.oldValue = oldValue;
                this.newValue = newValue;
                this.id = "ValueChanged";
                this.className = "org.opendolphin.core.comm.ValueChangedCommand";
            }
            return ValueChangedCommand;
        })(cmd.dolphin.Command);
        dolphin.ValueChangedCommand = ValueChangedCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=ValueChangedCommand.js.map
