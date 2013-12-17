var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var CallNamedActionCommand = (function (_super) {
            __extends(CallNamedActionCommand, _super);
            function CallNamedActionCommand(actionName) {
                _super.call(this);
                this.actionName = actionName;
                this.id = 'CallNamedAction';
                this.className = "org.opendolphin.core.comm.CallNamedActionCommand";
            }
            return CallNamedActionCommand;
        })(cmd.dolphin.Command);
        dolphin.CallNamedActionCommand = CallNamedActionCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=CallNamedActionCommand.js.map
