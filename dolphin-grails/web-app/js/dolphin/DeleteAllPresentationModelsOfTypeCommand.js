var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var DeleteAllPresentationModelsOfTypeCommand = (function (_super) {
            __extends(DeleteAllPresentationModelsOfTypeCommand, _super);
            function DeleteAllPresentationModelsOfTypeCommand(pmType) {
                _super.call(this);
                this.pmType = pmType;
                this.id = 'DeleteAllPresentationModelsOfTypeCommand';
                this.className = "org.opendolphin.core.comm.DeleteAllPresentationModelsOfTypeCommand";
            }
            return DeleteAllPresentationModelsOfTypeCommand;
        })(cmd.dolphin.Command);
        dolphin.DeleteAllPresentationModelsOfTypeCommand = DeleteAllPresentationModelsOfTypeCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=DeleteAllPresentationModelsOfTypeCommand.js.map
