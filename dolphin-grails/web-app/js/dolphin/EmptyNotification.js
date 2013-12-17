var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var EmptyNotification = (function (_super) {
            __extends(EmptyNotification, _super);
            function EmptyNotification() {
                _super.call(this);
                this.id = "Empty";
                this.className = "org.opendolphin.core.comm.EmptyNotification";
            }
            return EmptyNotification;
        })(cmd.dolphin.Command);
        dolphin.EmptyNotification = EmptyNotification;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=EmptyNotification.js.map
