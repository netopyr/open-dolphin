var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/Command"], function(require, exports, __cmd__) {
    
    
    var cmd = __cmd__;
    (function (dolphin) {
        var CreatePresentationModelCommand = (function (_super) {
            __extends(CreatePresentationModelCommand, _super);
            function CreatePresentationModelCommand(presentationModel) {
                _super.call(this);
                this.attributes = [];
                this.clientSideOnly = false;
                this.id = "CreatePresentationModel";
                this.className = "org.opendolphin.core.comm.CreatePresentationModelCommand";
                this.pmId = presentationModel.id;
                this.pmType = presentationModel.presentationModelType;

                var attrs = this.attributes;
                presentationModel.getAttributes().forEach(function (attr) {
                    attrs.push({
                        propertyName: attr.propertyName,
                        id: attr.id,
                        qualifier: attr.qualifier,
                        value: attr.getValue(),
                        tag: attr.tag
                    });
                });
            }
            return CreatePresentationModelCommand;
        })(cmd.dolphin.Command);
        dolphin.CreatePresentationModelCommand = CreatePresentationModelCommand;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=CreatePresentationModelCommand.js.map
