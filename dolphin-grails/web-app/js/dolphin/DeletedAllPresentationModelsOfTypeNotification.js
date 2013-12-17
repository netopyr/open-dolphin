var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var DeletedAllPresentationModelsOfTypeNotification = (function (_super) {
            __extends(DeletedAllPresentationModelsOfTypeNotification, _super);
            function DeletedAllPresentationModelsOfTypeNotification(pmType) {
                _super.call(this);
                this.pmType = pmType;
                this.id = 'DeletedAllPresentationModels';
                this.className = "org.opendolphin.core.comm.DeletedAllPresentationModelsOfTypeNotification";
            }
            return DeletedAllPresentationModelsOfTypeNotification;
        })(cmd.dolphin.Command);
        dolphin.DeletedAllPresentationModelsOfTypeNotification = DeletedAllPresentationModelsOfTypeNotification;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=DeletedAllPresentationModelsOfTypeNotification.js.map
