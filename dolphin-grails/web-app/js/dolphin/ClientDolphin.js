var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "../../js/dolphin/NamedCommand", "../../js/dolphin/EmptyNotification", "../../js/dolphin/ClientPresentationModel", "../../js/dolphin/Dolphin", "../../js/dolphin/AttributeCreatedNotification"], function(require, exports, __namedCmd__, __emptyNot__, __pm__, __dol__, __acn__) {
    var namedCmd = __namedCmd__;
    var emptyNot = __emptyNot__;
    var pm = __pm__;
    
    
    
    var dol = __dol__;
    var acn = __acn__;

    (function (dolphin) {
        var ClientDolphin = (function (_super) {
            __extends(ClientDolphin, _super);
            function ClientDolphin() {
                _super.apply(this, arguments);
            }
            ClientDolphin.prototype.setClientConnector = function (clientConnector) {
                this.clientConnector = clientConnector;
            };

            ClientDolphin.prototype.getClientConnector = function () {
                return this.clientConnector;
            };

            ClientDolphin.prototype.send = function (commandName, onFinished) {
                this.clientConnector.send(new namedCmd.dolphin.NamedCommand(commandName), onFinished);
            };

            ClientDolphin.prototype.sendEmpty = function (onFinished) {
                this.clientConnector.send(new emptyNot.dolphin.EmptyNotification(), onFinished);
            };

            ClientDolphin.prototype.presentationModel = function (id, type) {
                var attributes = [];
                for (var _i = 0; _i < (arguments.length - 2); _i++) {
                    attributes[_i] = arguments[_i + 2];
                }
                var model = new pm.dolphin.ClientPresentationModel(id, type);
                if (attributes && attributes.length > 0) {
                    attributes.forEach(function (attribute) {
                        model.addAttribute(attribute);
                    });
                }
                this.getClientModelStore().add(model);
                return model;
            };

            ClientDolphin.prototype.addAttributeToModel = function (presentationModel, clientAttribute) {
                presentationModel.addAttribute(clientAttribute);

                if (!presentationModel.clientSideOnly) {
                    this.clientConnector.send(new acn.dolphin.AttributeCreatedNotification(presentationModel.id, clientAttribute.id, clientAttribute.propertyName, clientAttribute.getValue(), clientAttribute.qualifier, clientAttribute.tag), null);
                }
            };
            return ClientDolphin;
        })(dol.dolphin.Dolphin);
        dolphin.ClientDolphin = ClientDolphin;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=ClientDolphin.js.map
