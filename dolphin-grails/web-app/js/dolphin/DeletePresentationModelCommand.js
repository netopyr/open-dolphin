var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var DeletePresentationModelCommand = (function (_super) {
            __extends(DeletePresentationModelCommand, _super);
            function DeletePresentationModelCommand(pmId) {
                _super.call(this);
                this.pmId = pmId;
                this.id = 'DeletePresentationModel';
                this.className = "org.opendolphin.core.comm.DeletePresentationModelCommand";
            }
            return DeletePresentationModelCommand;
        })(cmd.dolphin.Command);
        dolphin.DeletePresentationModelCommand = DeletePresentationModelCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=DeletePresentationModelCommand.js.map
