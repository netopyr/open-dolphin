var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var SavedPresentationModelNotification = (function (_super) {
            __extends(SavedPresentationModelNotification, _super);
            function SavedPresentationModelNotification(pmId) {
                _super.call(this);
                this.pmId = pmId;
                this.id = 'SavedPresentationModel';
                this.className = "org.opendolphin.core.comm.SavedPresentationModelNotification";
            }
            return SavedPresentationModelNotification;
        })(cmd.dolphin.Command);
        dolphin.SavedPresentationModelNotification = SavedPresentationModelNotification;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=SavedPresentationModelNotification.js.map
