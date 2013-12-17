var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var SwitchPresentationModelCommand = (function (_super) {
            __extends(SwitchPresentationModelCommand, _super);
            function SwitchPresentationModelCommand(pmId, sourcePmId) {
                _super.call(this);
                this.pmId = pmId;
                this.sourcePmId = sourcePmId;
                this.id = 'SwitchPresentationModel';
                this.className = "org.opendolphin.core.comm.SwitchPresentationModelCommand";
            }
            return SwitchPresentationModelCommand;
        })(cmd.dolphin.Command);
        dolphin.SwitchPresentationModelCommand = SwitchPresentationModelCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=SwitchPresentationModelCommand.js.map
