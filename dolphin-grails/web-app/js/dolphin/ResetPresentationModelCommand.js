var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var ResetPresentationModelCommand = (function (_super) {
            __extends(ResetPresentationModelCommand, _super);
            function ResetPresentationModelCommand(pmId) {
                _super.call(this);
                this.pmId = pmId;
                this.id = 'ResetPresentationModel';
                this.className = "org.opendolphin.core.comm.ResetPresentationModelCommand";
            }
            return ResetPresentationModelCommand;
        })(cmd.dolphin.Command);
        dolphin.ResetPresentationModelCommand = ResetPresentationModelCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=ResetPresentationModelCommand.js.map
