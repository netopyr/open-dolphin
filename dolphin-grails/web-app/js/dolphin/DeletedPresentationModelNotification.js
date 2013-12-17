var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var DeletedPresentationModelNotification = (function (_super) {
            __extends(DeletedPresentationModelNotification, _super);
            function DeletedPresentationModelNotification(pmId) {
                _super.call(this);
                this.pmId = pmId;
                this.id = 'DeletedPresentationModel';
                this.className = "org.opendolphin.core.comm.DeletedPresentationModelNotification";
            }
            return DeletedPresentationModelNotification;
        })(cmd.dolphin.Command);
        dolphin.DeletedPresentationModelNotification = DeletedPresentationModelNotification;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=DeletedPresentationModelNotification.js.map
