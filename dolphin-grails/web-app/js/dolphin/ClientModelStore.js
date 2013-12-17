define(["require", "exports", "../../js/dolphin/CreatePresentationModelCommand", "../../js/dolphin/ValueChangedCommand", "../../js/dolphin/ChangeAttributeMetadataCommand", "../../js/dolphin/Attribute", "../../js/dolphin/Map", "../../js/dolphin/DeletedAllPresentationModelsOfTypeNotification", "../../js/dolphin/EventBus"], function(require, exports, __createPMCmd__, __valueChangedCmd__, __changeAttMD__, __attr__, __map__, __dpmoftn__, __bus__) {
    
    
    
    var createPMCmd = __createPMCmd__;
    
    var valueChangedCmd = __valueChangedCmd__;
    var changeAttMD = __changeAttMD__;
    var attr = __attr__;
    var map = __map__;
    var dpmoftn = __dpmoftn__;
    var bus = __bus__;
    

    (function (dolphin) {
        (function (Type) {
            Type[Type["ADDED"] = 0] = "ADDED";
            Type[Type["REMOVED"] = 1] = "REMOVED";
        })(dolphin.Type || (dolphin.Type = {}));
        var Type = dolphin.Type;

        var ClientModelStore = (function () {
            function ClientModelStore(clientDolphin) {
                this.clientDolphin = clientDolphin;
                this.presentationModels = new map.dolphin.Map();
                this.presentationModelsPerType = new map.dolphin.Map();
                this.attributesPerId = new map.dolphin.Map();
                this.attributesPerQualifier = new map.dolphin.Map();
                this.modelStoreChangeBus = new bus.dolphin.EventBus();
            }
            ClientModelStore.prototype.getClientDolphin = function () {
                return this.clientDolphin;
            };

            ClientModelStore.prototype.registerModel = function (model) {
                var _this = this;
                if (model.clientSideOnly) {
                    return;
                }
                var connector = this.clientDolphin.getClientConnector();
                var createPMCommand = new createPMCmd.dolphin.CreatePresentationModelCommand(model);
                console.log("about to send create presentation model command", createPMCommand);
                connector.send(createPMCommand, null);
                model.getAttributes().forEach(function (attribute) {
                    _this.addAttributeById(attribute);
                    attribute.onValueChange(function (evt) {
                        var valueChangeCommand = new valueChangedCmd.dolphin.ValueChangedCommand(attribute.id, evt.oldValue, evt.newValue);
                        connector.send(valueChangeCommand, null);

                        if (attribute.qualifier) {
                            _this.addAttributeByQualifier(attribute);
                            var attrs = _this.findAttributesByFilter(function (attr) {
                                return attr !== attribute && attr.qualifier === attribute.qualifier;
                            });
                            attrs.forEach(function (attr) {
                                attr.setValue(attribute.getValue());
                            });
                        }
                    });

                    attribute.onQualifierChange(function (evt) {
                        var changeAttrMetadataCmd = new changeAttMD.dolphin.ChangeAttributeMetadataCommand(attribute.id, attr.dolphin.Attribute.QUALIFIER_PROPERTY, evt.newValue);
                        connector.send(changeAttrMetadataCmd, null);
                    });
                });
            };

            ClientModelStore.prototype.add = function (model) {
                if (!model) {
                    return false;
                }
                if (this.presentationModels.containsKey(model.id)) {
                    alert("There already is a PM with id " + model.id);
                }
                var added = false;
                if (!this.presentationModels.containsValue(model)) {
                    this.presentationModels.put(model.id, model);
                    this.addPresentationModelByType(model);
                    this.registerModel(model);

                    this.modelStoreChangeBus.trigger({ 'eventType': Type.ADDED, 'clientPresentationModel': model });
                    added = true;
                }

                console.log("client presentation model added and registered");
                return added;
            };

            ClientModelStore.prototype.remove = function (model) {
                var _this = this;
                if (!model) {
                    return false;
                }
                var removed = false;
                if (this.presentationModels.containsKey(model.id)) {
                    this.removePresentationModelByType(model);
                    this.presentationModels.remove(model.id);
                    model.getAttributes().forEach(function (attribute) {
                        //todo property change listener
                        _this.removeAttributeById(attribute);
                        if (attribute.qualifier) {
                            _this.removeAttributeByQualifier(attribute);
                        }
                    });

                    this.modelStoreChangeBus.trigger({ 'eventType': Type.REMOVED, 'clientPresentationModel': model });
                    removed = true;
                }
                return removed;
            };

            ClientModelStore.prototype.findAttributesByFilter = function (filter) {
                var matches = [];
                this.presentationModels.forEach(function (key, model) {
                    model.getAttributes().forEach(function (attr) {
                        if (filter(attr)) {
                            matches.push(attr);
                        }
                    });
                });
                return matches;
            };

            ClientModelStore.prototype.addPresentationModelByType = function (model) {
                if (!model) {
                    return;
                }
                var type = model.presentationModelType;
                if (!type) {
                    return;
                }
                var presentationModels = this.presentationModelsPerType.get(type);
                if (!presentationModels) {
                    presentationModels = [];
                    this.presentationModelsPerType.put(type, presentationModels);
                }
                if (!(presentationModels.indexOf(model) > -1)) {
                    presentationModels.push(model);
                }
            };

            ClientModelStore.prototype.removePresentationModelByType = function (model) {
                if (!model || !(model.presentationModelType)) {
                    return;
                }

                var presentationModels = this.presentationModelsPerType.get(model.presentationModelType);
                if (!presentationModels) {
                    return;
                }
                if (presentationModels.length > -1) {
                    presentationModels.splice(presentationModels.indexOf(model), 1);
                }
                if (presentationModels.length === 0) {
                    this.presentationModelsPerType.remove(model.presentationModelType);
                }
            };

            ClientModelStore.prototype.listPresentationModelIds = function () {
                return this.presentationModels.keySet().slice(0);
            };

            ClientModelStore.prototype.listPresentationModels = function () {
                return this.presentationModels.values();
            };

            ClientModelStore.prototype.findPresentationModelById = function (id) {
                return this.presentationModels.get(id);
            };

            ClientModelStore.prototype.findAllPresentationModelByType = function (type) {
                if (!type || !this.presentationModelsPerType.containsKey(type)) {
                    return [];
                }
                return this.presentationModelsPerType.get(type).slice(0);
            };

            ClientModelStore.prototype.deleteAllPresentationModelOfType = function (presentationModelType) {
                var _this = this;
                var presentationModels = this.findAllPresentationModelByType(presentationModelType);
                presentationModels.forEach(function (pm) {
                    _this.delete(pm, false);
                });
            };

            ClientModelStore.prototype.delete = function (model, notify) {
                if (!model) {
                    return;
                }
                if (this.containsPresentationModel(model.id)) {
                    this.remove(model);
                    if (!notify || model.clientSideOnly) {
                        return;
                    }
                    var connector = this.clientDolphin.getClientConnector();
                    connector.send(new dpmoftn.dolphin.DeletedAllPresentationModelsOfTypeNotification(model.presentationModelType), undefined);
                }
            };

            ClientModelStore.prototype.containsPresentationModel = function (id) {
                return this.presentationModels.containsKey(id);
            };

            ClientModelStore.prototype.addAttributeById = function (attribute) {
                if (!attribute || this.attributesPerId.containsKey(attribute.id)) {
                    return;
                }
                this.attributesPerId.put(attribute.id, attribute);
            };

            ClientModelStore.prototype.removeAttributeById = function (attribute) {
                if (!attribute || !this.attributesPerId.containsKey(attribute.id)) {
                    return;
                }
                this.attributesPerId.remove(attribute.id);
            };

            ClientModelStore.prototype.findAttributeById = function (id) {
                return this.attributesPerId.get(id);
            };

            ClientModelStore.prototype.addAttributeByQualifier = function (attribute) {
                if (!attribute || !attribute.qualifier) {
                    return;
                }
                var attributes = this.attributesPerQualifier.get(attribute.qualifier);
                if (!attributes) {
                    attributes = [];
                    this.attributesPerQualifier.put(attribute.qualifier, attributes);
                }
                if (!(attributes.indexOf(attribute) > -1)) {
                    attributes.push(attribute);
                }
            };

            ClientModelStore.prototype.removeAttributeByQualifier = function (attribute) {
                if (!attribute || !attribute.qualifier) {
                    return;
                }
                var attributes = this.attributesPerQualifier.get(attribute.qualifier);
                if (!attributes) {
                    return;
                }
                if (attributes.length > -1) {
                    attributes.splice(attributes.indexOf(attribute), 1);
                }
                if (attributes.length === 0) {
                    this.attributesPerQualifier.remove(attribute.qualifier);
                }
            };

            ClientModelStore.prototype.findAllAttributeByQualifier = function (qualifier) {
                if (!qualifier || !this.attributesPerQualifier.containsKey(qualifier)) {
                    return [];
                }
                return this.attributesPerQualifier.get(qualifier).slice(0);
            };

            ClientModelStore.prototype.onModelStoreChange = function (eventHandler) {
                this.modelStoreChangeBus.onEvent(eventHandler);
            };
            return ClientModelStore;
        })();
        dolphin.ClientModelStore = ClientModelStore;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=ClientModelStore.js.map
