var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    var cmd = __cmd__;
    (function (dolphin) {
        var AttributeCreatedNotification = (function (_super) {
            __extends(AttributeCreatedNotification, _super);
            function AttributeCreatedNotification(pmId, attributeId, propertyName, newValue, qualifier, tag) {
                if (typeof tag === "undefined") { tag = "VALUE"; }
                _super.call(this);
                this.pmId = pmId;
                this.attributeId = attributeId;
                this.propertyName = propertyName;
                this.newValue = newValue;
                this.qualifier = qualifier;
                this.tag = tag;
                this.id = 'AttributeCreated';
                this.className = "org.opendolphin.core.comm.AttributeCreatedNotification";
            }
            return AttributeCreatedNotification;
        })(cmd.dolphin.Command);
        dolphin.AttributeCreatedNotification = AttributeCreatedNotification;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=AttributeCreatedNotification.js.map
