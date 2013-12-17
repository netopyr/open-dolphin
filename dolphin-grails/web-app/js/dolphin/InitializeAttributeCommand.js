var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    
    
    var cmd = __cmd__;
    (function (dolphin) {
        var InitializeAttributeCommand = (function (_super) {
            __extends(InitializeAttributeCommand, _super);
            function InitializeAttributeCommand(pmId, pmType, propertyName, qualifier, newValue, tag) {
                if (typeof tag === "undefined") { tag = "VALUE"; }
                _super.call(this);
                this.pmId = pmId;
                this.pmType = pmType;
                this.propertyName = propertyName;
                this.qualifier = qualifier;
                this.newValue = newValue;
                this.tag = tag;
                this.id = 'InitializeAttribute';
                this.className = "org.opendolphin.core.comm.InitializeAttributeCommand";
            }
            return InitializeAttributeCommand;
        })(cmd.dolphin.Command);
        dolphin.InitializeAttributeCommand = InitializeAttributeCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=InitializeAttributeCommand.js.map
