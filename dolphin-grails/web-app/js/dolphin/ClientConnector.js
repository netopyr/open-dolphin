define(["require", "exports", "../../js/dolphin/ClientPresentationModel", "../../js/dolphin/Codec", "../../js/dolphin/CallNamedActionCommand", "../../js/dolphin/AttributeMetadataChangedCommand", "../../js/dolphin/ClientAttribute", "../../js/dolphin/PresentationModelResetedCommand", "../../js/dolphin/SavedPresentationModelNotification", "../../js/dolphin/InitializeAttributeCommand", "../../js/dolphin/SwitchPresentationModelCommand", "../../js/dolphin/BaseValueChangedCommand", "../../js/dolphin/DeleteAllPresentationModelsOfTypeCommand", "../../js/dolphin/DeletePresentationModelCommand"], function(require, exports, __cpm__, __cod__, __cna__, __amdcc__, __ca__, __pmrc__, __spmn__, __iac__, __spmc__, __bvcc__, __dapmc__, __dpmc__) {
    var cpm = __cpm__;
    
    var cod = __cod__;
    var cna = __cna__;
    
    var amdcc = __amdcc__;
    var ca = __ca__;
    var pmrc = __pmrc__;
    var spmn = __spmn__;
    var iac = __iac__;
    var spmc = __spmc__;
    var bvcc = __bvcc__;
    
    
    var dapmc = __dapmc__;
    var dpmc = __dpmc__;
    

    (function (dolphin) {
        var ClientConnector = (function () {
            function ClientConnector(transmitter, clientDolphin) {
                this.commandQueue = [];
                this.currentlySending = false;
                this.transmitter = transmitter;
                this.clientDolphin = clientDolphin;
                this.codec = new cod.dolphin.Codec();
            }
            ClientConnector.prototype.send = function (command, onFinished) {
                this.commandQueue.push({ command: command, handler: onFinished });
                if (this.currentlySending)
                    return;
                this.doSendNext();
            };

            ClientConnector.prototype.doSendNext = function () {
                var _this = this;
                if (this.commandQueue.length < 1) {
                    this.currentlySending = false;
                    return;
                }
                this.currentlySending = true;
                var cmdAndHandler = this.commandQueue.shift();
                this.transmitter.transmit([cmdAndHandler.command], function (response) {
                    console.log("server response: [" + response.map(function (it) {
                        return it.id;
                    }).join(", ") + "] ");

                    var touchedPMs = [];
                    response.forEach(function (command) {
                        var touched = _this.handle(command);
                        if (touched)
                            touchedPMs.push(touched);
                    });

                    var callback = cmdAndHandler.handler;
                    if (callback) {
                        callback.onFinished(touchedPMs);
                    }

                    _this.doSendNext();
                });
            };

            ClientConnector.prototype.handle = function (command) {
                if (command instanceof dpmc.dolphin.DeletePresentationModelCommand) {
                    return this.handleDeletePresentationModelCommand(command);
                }
                if (command instanceof dapmc.dolphin.DeleteAllPresentationModelsOfTypeCommand) {
                    return this.handleDeleteAllPresentationModelOfTypeCommand(command);
                }
                if (command.id == "CreatePresentationModel") {
                    return this.handleCreatePresentationModelCommand(command);
                }
                if (command.id == "ValueChanged") {
                    return this.handleValueChangedCommand(command);
                }
                if (command instanceof bvcc.dolphin.BaseValueChangedCommand) {
                    return this.handleBaseValueChangedCommand(command);
                }
                if (command instanceof spmc.dolphin.SwitchPresentationModelCommand) {
                    return this.handleSwitchPresentationModelCommand(command);
                }
                if (command instanceof iac.dolphin.InitializeAttributeCommand) {
                    return this.handleInitializeAttributeCommand(command);
                }
                if (command instanceof spmn.dolphin.SavedPresentationModelNotification) {
                    return this.handleSavedPresentationModelNotification(command);
                }
                if (command instanceof pmrc.dolphin.PresentationModelResetedCommand) {
                    return this.handlePresentationModelResetedCommand(command);
                }
                if (command instanceof amdcc.dolphin.AttributeMetadataChangedCommand) {
                    return this.handleAttributeMetadataChangedCommand(command);
                }
                if (command instanceof cna.dolphin.CallNamedActionCommand) {
                    return this.handleCallNamedActionCommand(command);
                }

                return null;
            };
            ClientConnector.prototype.handleDataCommand = function () {
                //todo: to be implement
            };
            ClientConnector.prototype.handleDeletePresentationModelCommand = function (serverCommand) {
                var model = this.clientDolphin.findPresentationModelById(serverCommand.pmId);
                if (!model)
                    return null;
                this.clientDolphin.getClientModelStore().delete(model, true);
                return model;
            };
            ClientConnector.prototype.handleDeleteAllPresentationModelOfTypeCommand = function (serverCommand) {
                this.clientDolphin.deleteAllPresentationModelOfType(serverCommand.pmType);
                return null;
            };
            ClientConnector.prototype.handleCreatePresentationModelCommand = function (serverCommand) {
                if (this.clientDolphin.getClientModelStore().containsPresentationModel(serverCommand.pmId)) {
                    throw new Error("There already is a presentation model with id " + serverCommand.pmId + "  known to the client.");
                }
                var attributes = [];
                serverCommand.attributes.forEach(function (attr) {
                    var clientAttribute = new ca.dolphin.ClientAttribute(attr.propertyName, attr.qualifier, attr.value, attr.tag ? attr.tag : "VALUE");
                    attributes.push(clientAttribute);
                });
                var clientPm = new cpm.dolphin.ClientPresentationModel(serverCommand.pmId, serverCommand.pmType);
                clientPm.addAttributes(attributes);
                if (serverCommand.clientSideOnly) {
                    clientPm.clientSideOnly = true;
                }
                this.clientDolphin.getClientModelStore().add(clientPm);
                this.clientDolphin.updateQualifier(clientPm);
                return clientPm;
            };
            ClientConnector.prototype.handleValueChangedCommand = function (serverCommand) {
                console.log("in value change");
                var clientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
                if (!clientAttribute) {
                    console.log("attribute with id " + serverCommand.attributeId + " not found, cannot update old value " + serverCommand.oldValue + " to new value " + serverCommand.newValue);
                    return null;
                }
                console.log("updating " + clientAttribute.propertyName + " id " + serverCommand.attributeId + " from " + clientAttribute.getValue() + " to " + serverCommand.newValue);
                clientAttribute.setValue(serverCommand.newValue);
                return null;
            };
            ClientConnector.prototype.handleBaseValueChangedCommand = function (serverCommand) {
                var clientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
                if (!clientAttribute) {
                    console.log("attribute with id " + serverCommand.attributeId + " not found, cannot set initial value.");
                    return null;
                }
                console.log("updating id " + serverCommand.attributeId + " setting initial value to " + clientAttribute.getValue());
                clientAttribute.rebase();
                return null;
            };
            ClientConnector.prototype.handleSwitchPresentationModelCommand = function (serverCommand) {
                var switchPm = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
                if (!switchPm) {
                    console.log("switch model with id " + serverCommand.pmId + " not found, cannot switch.");
                    return null;
                }
                var sourcePm = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.sourcePmId);
                if (!sourcePm) {
                    console.log("source model with id " + serverCommand.sourcePmId + " not found, cannot switch.");
                    return null;
                }
                switchPm.syncWith(sourcePm);
                return switchPm;
            };
            ClientConnector.prototype.handleInitializeAttributeCommand = function (serverCommand) {
                var attribute = new ca.dolphin.ClientAttribute(serverCommand.propertyName, serverCommand.qualifier, serverCommand.newValue, serverCommand.tag);
                if (serverCommand.qualifier) {
                    var attributesCopy = this.clientDolphin.getClientModelStore().findAllAttributeByQualifier(serverCommand.qualifier);
                    if (attributesCopy) {
                        if (!serverCommand.newValue) {
                            var attr = attributesCopy.shift();
                            if (attr) {
                                attribute.setValue(attr.getValue());
                            }
                        } else {
                            attributesCopy.forEach(function (attr) {
                                attr.setValue(attribute.getValue());
                            });
                        }
                    }
                }
                var presentationModel;
                if (serverCommand.pmId) {
                    presentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
                }
                if (!presentationModel) {
                    presentationModel = new cpm.dolphin.ClientPresentationModel(serverCommand.pmId, serverCommand.pmType);
                    this.clientDolphin.getClientModelStore().add(presentationModel);
                }
                this.clientDolphin.addAttributeToModel(presentationModel, attribute);
                this.clientDolphin.updateQualifier(presentationModel);
                return presentationModel;
            };
            ClientConnector.prototype.handleSavedPresentationModelNotification = function (serverCommand) {
                if (!serverCommand.pmId)
                    return null;
                var model = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
                if (!model) {
                    console.log("model with id " + serverCommand.pmId + " not found, cannot rebase.");
                    return null;
                }
                model.rebase();
                return model;
            };
            ClientConnector.prototype.handlePresentationModelResetedCommand = function (serverCommand) {
                if (!serverCommand.pmId)
                    return null;
                var model = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
                if (!model) {
                    console.log("model with id " + serverCommand.pmId + " not found, cannot reset.");
                    return null;
                }
                model.reset();
                return model;
            };

            //todo: verify the logic
            ClientConnector.prototype.handleAttributeMetadataChangedCommand = function (serverCommand) {
                var clientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
                if (!clientAttribute)
                    return null;
                clientAttribute[serverCommand.metadataName] = serverCommand.value;
                return null;
            };
            ClientConnector.prototype.handleCallNamedActionCommand = function (serverCommand) {
                this.clientDolphin.send(serverCommand.actionName, null);
                return null;
            };
            return ClientConnector;
        })();
        dolphin.ClientConnector = ClientConnector;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=ClientConnector.js.map
