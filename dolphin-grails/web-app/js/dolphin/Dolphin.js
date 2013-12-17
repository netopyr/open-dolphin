define(["require", "exports"], function(require, exports) {
    
    

    (function (dolphin) {
        var Dolphin = (function () {
            function Dolphin() {
            }
            Dolphin.prototype.setClientModelStore = function (clientModelStore) {
                this.clientModelStore = clientModelStore;
            };

            Dolphin.prototype.getClientModelStore = function () {
                return this.clientModelStore;
            };

            Dolphin.prototype.listPresentationModelIds = function () {
                return this.getClientModelStore().listPresentationModelIds();
            };

            Dolphin.prototype.findAllPresentationModelByType = function (presentationModelType) {
                return this.getClientModelStore().findAllPresentationModelByType(presentationModelType);
            };

            Dolphin.prototype.getAt = function (id) {
                return this.findPresentationModelById(id);
            };

            Dolphin.prototype.findPresentationModelById = function (id) {
                return this.getClientModelStore().findPresentationModelById(id);
            };
            Dolphin.prototype.delete = function (modelToDelete) {
                this.getClientModelStore().delete(modelToDelete, false);
            };

            Dolphin.prototype.deleteAllPresentationModelOfType = function (presentationModelType) {
                this.getClientModelStore().deleteAllPresentationModelOfType(presentationModelType);
            };
            Dolphin.prototype.updateQualifier = function (presentationModel) {
                var _this = this;
                presentationModel.getAttributes().forEach(function (sourceAttribute) {
                    if (!sourceAttribute.qualifier)
                        return;
                    var attributes = _this.getClientModelStore().findAllAttributeByQualifier(sourceAttribute.qualifier);
                    attributes.forEach(function (targetAttribute) {
                        if (targetAttribute.tag != sourceAttribute.tag)
                            return;
                        targetAttribute.setValue(sourceAttribute.getValue());
                    });
                });
            };
            return Dolphin;
        })();
        dolphin.Dolphin = Dolphin;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=Dolphin.js.map
