import cpm    = require("../../js/dolphin/ClientPresentationModel");
import cmd    = require("../../js/dolphin/Command");
import cod    = require("../../js/dolphin/Codec");
import cna    = require("../../js/dolphin/CallNamedActionCommand");
import cd     = require("../../js/dolphin/ClientDolphin");
import amdcc  = require("../../js/dolphin/AttributeMetadataChangedCommand");
import ca     = require("../../js/dolphin/ClientAttribute");
import pmrc   = require("../../js/dolphin/PresentationModelResetedCommand");
import spmn   = require("../../js/dolphin/SavedPresentationModelNotification");
import iac    = require("../../js/dolphin/InitializeAttributeCommand");
import spmc   = require("../../js/dolphin/SwitchPresentationModelCommand");
import bvcc   = require("../../js/dolphin/BaseValueChangedCommand");
import vcc    = require("../../js/dolphin/ValueChangedCommand");
import dapm   = require("../../js/dolphin/DeleteAllPresentationModelsOfTypeCommand");
import dapmc  = require("../../js/dolphin/DeleteAllPresentationModelsOfTypeCommand");
import dpmc   = require("../../js/dolphin/DeletePresentationModelCommand");
import cpmc   = require("../../js/dolphin/CreatePresentationModelCommand");

export module dolphin {

    // todo dk: this interface may disappear
    export interface OnFinishedAdapter {
        onFinished(models:cpm.dolphin.ClientPresentationModel[]):void
        onFinishedData(listOfData:any[]):void
    }

    interface CommandAndHandler {
        command : cmd.dolphin.Command;
        handler : OnFinishedAdapter;
    }

    export interface Transmitter {
        transmit(commands:cmd.dolphin.Command[], onDone:(result:cmd.dolphin.Command[]) => void) : void ;
    }

    export class ClientConnector {

        private commandQueue:CommandAndHandler[] = [];
        private currentlySending:boolean = false;
        private transmitter:Transmitter;
        private codec:cod.dolphin.Codec;
        private clientDolphin:cd.dolphin.ClientDolphin;


        constructor(transmitter:Transmitter, clientDolphin:cd.dolphin.ClientDolphin) {
            this.transmitter = transmitter;
            this.clientDolphin = clientDolphin;
            this.codec = new cod.dolphin.Codec();
        }

        send(command:cmd.dolphin.Command, onFinished:OnFinishedAdapter) {
            this.commandQueue.push({command: command, handler: onFinished });
            if (this.currentlySending) return;
            this.doSendNext();
        }

        private doSendNext() {
            if (this.commandQueue.length < 1) {
                this.currentlySending = false;
                return;
            }
            this.currentlySending = true;
            var cmdAndHandler = this.commandQueue.shift();
            this.transmitter.transmit([cmdAndHandler.command], (response:cmd.dolphin.Command[]) => {
                console.log("server response: [" + response.map(it => it.id).join(", ") + "] ");

                var touchedPMs : cpm.dolphin.ClientPresentationModel[] = []
                response.forEach((command:cmd.dolphin.Command) => {
                    var touched = this.handle(command);
                    if (touched) touchedPMs.push(touched);
                });

                var callback = cmdAndHandler.handler;
                if (callback) {
                    callback.onFinished(touchedPMs); // todo: make them unique?
                }

                this.doSendNext();  // recursive call: fetch the next in line
            });
        }



        handle(command:cmd.dolphin.Command): cpm.dolphin.ClientPresentationModel{
            if(command instanceof dpmc.dolphin.DeletePresentationModelCommand){
                return this.handleDeletePresentationModelCommand(<dpmc.dolphin.DeletePresentationModelCommand>command);
            }
            if(command instanceof dapmc.dolphin.DeleteAllPresentationModelsOfTypeCommand){
                return this.handleDeleteAllPresentationModelOfTypeCommand(<dapmc.dolphin.DeleteAllPresentationModelsOfTypeCommand>command);
            }
            if(command.id == "CreatePresentationModel"){
                return this.handleCreatePresentationModelCommand(<cpmc.dolphin.CreatePresentationModelCommand>command);
            }
            if(command.id == "ValueChanged"){
                return this.handleValueChangedCommand(<vcc.dolphin.ValueChangedCommand>command);
            }
            if(command instanceof bvcc.dolphin.BaseValueChangedCommand){
                return this.handleBaseValueChangedCommand(<bvcc.dolphin.BaseValueChangedCommand>command);
            }
            if(command instanceof spmc.dolphin.SwitchPresentationModelCommand){
                return this.handleSwitchPresentationModelCommand(<spmc.dolphin.SwitchPresentationModelCommand>command);
            }
            if(command instanceof iac.dolphin.InitializeAttributeCommand){
                return this.handleInitializeAttributeCommand(<iac.dolphin.InitializeAttributeCommand>command);
            }
            if(command instanceof spmn.dolphin.SavedPresentationModelNotification){
                return this.handleSavedPresentationModelNotification(<spmn.dolphin.SavedPresentationModelNotification>command);
            }
            if(command instanceof pmrc.dolphin.PresentationModelResetedCommand){
                return this.handlePresentationModelResetedCommand(<pmrc.dolphin.PresentationModelResetedCommand>command);
            }
            if(command instanceof amdcc.dolphin.AttributeMetadataChangedCommand){
                return this.handleAttributeMetadataChangedCommand(<amdcc.dolphin.AttributeMetadataChangedCommand>command);
            }
            if(command instanceof cna.dolphin.CallNamedActionCommand){
                return this.handleCallNamedActionCommand(<cna.dolphin.CallNamedActionCommand>command);
            }

            return null;
        }
        private handleDataCommand(){
            //todo: to be implement
        }
        private handleDeletePresentationModelCommand(serverCommand:dpmc.dolphin.DeletePresentationModelCommand):cpm.dolphin.ClientPresentationModel{
            var model:cpm.dolphin.ClientPresentationModel =  this.clientDolphin.findPresentationModelById(serverCommand.pmId);
            if(!model) return null;
            this.clientDolphin.getClientModelStore().delete(model, true);
            return model;
        }
        private handleDeleteAllPresentationModelOfTypeCommand(serverCommand:dapmc.dolphin.DeleteAllPresentationModelsOfTypeCommand){
            this.clientDolphin.deleteAllPresentationModelOfType(serverCommand.pmType);
            return null;
        }
        private handleCreatePresentationModelCommand(serverCommand:cpmc.dolphin.CreatePresentationModelCommand):cpm.dolphin.ClientPresentationModel{
            if(this.clientDolphin.getClientModelStore().containsPresentationModel(serverCommand.pmId)){
                throw new Error("There already is a presentation model with id "+serverCommand.pmId+"  known to the client.");
            }
            var attributes:ca.dolphin.ClientAttribute[] = [];
            serverCommand.attributes.forEach((attr) =>{
                var clientAttribute = new ca.dolphin.ClientAttribute(attr.propertyName,attr.qualifier,attr.value, attr.tag?attr.tag:"VALUE");
                attributes.push(clientAttribute);
            });
            var clientPm = new cpm.dolphin.ClientPresentationModel(serverCommand.pmId, serverCommand.pmType);
            clientPm.addAttributes(attributes);
            if(serverCommand.clientSideOnly){
                clientPm.clientSideOnly = true;
            }
            this.clientDolphin.getClientModelStore().add(clientPm);
            this.clientDolphin.updateQualifier(clientPm);
            return clientPm;
        }
        private handleValueChangedCommand(serverCommand:vcc.dolphin.ValueChangedCommand):cpm.dolphin.ClientPresentationModel{
            console.log("in value change")
            var clientAttribute: ca.dolphin.ClientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
            if(!clientAttribute){
                console.log("attribute with id "+serverCommand.attributeId+" not found, cannot update old value "+serverCommand.oldValue+" to new value "+serverCommand.newValue);
                return null;
            }
            console.log("updating "+clientAttribute.propertyName +" id "+serverCommand.attributeId+" from "+clientAttribute.getValue()+" to "+serverCommand.newValue);
            clientAttribute.setValue(serverCommand.newValue);
            return null;
        }
        private handleBaseValueChangedCommand(serverCommand:bvcc.dolphin.BaseValueChangedCommand):cpm.dolphin.ClientPresentationModel{
            var clientAttribute: ca.dolphin.ClientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
            if(!clientAttribute){
                console.log("attribute with id "+serverCommand.attributeId+" not found, cannot set initial value.");
                return null;
            }
            console.log("updating id "+serverCommand.attributeId+" setting initial value to "+clientAttribute.getValue());
            clientAttribute.rebase();
            return null;
        }
        private handleSwitchPresentationModelCommand(serverCommand:spmc.dolphin.SwitchPresentationModelCommand):cpm.dolphin.ClientPresentationModel{
            var switchPm:cpm.dolphin.ClientPresentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
            if(!switchPm){
                console.log("switch model with id "+serverCommand.pmId+" not found, cannot switch.");
                return null;
            }
            var sourcePm = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.sourcePmId);
            if(!sourcePm){
                console.log("source model with id "+serverCommand.sourcePmId+" not found, cannot switch.");
                return null;
            }
            switchPm.syncWith(sourcePm);
            return switchPm;
        }
        private handleInitializeAttributeCommand(serverCommand: iac.dolphin.InitializeAttributeCommand):cpm.dolphin.ClientPresentationModel{
            var attribute = new ca.dolphin.ClientAttribute(serverCommand.propertyName,serverCommand.qualifier,serverCommand.newValue, serverCommand.tag);
            if(serverCommand.qualifier){
                var attributesCopy:ca.dolphin.ClientAttribute[]= this.clientDolphin.getClientModelStore().findAllAttributeByQualifier(serverCommand.qualifier);
                if(attributesCopy){
                    if(!serverCommand.newValue){
                        var attr = attributesCopy.shift();
                        if(attr){
                            attribute.setValue(attr.getValue());
                        }
                    }else{
                        attributesCopy.forEach(attr =>{
                            attr.setValue(attribute.getValue());
                        });
                    }
                }
            }
            var presentationModel: cpm.dolphin.ClientPresentationModel;
            if(serverCommand.pmId){
                presentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
            }
            if(!presentationModel){
                presentationModel = new cpm.dolphin.ClientPresentationModel(serverCommand.pmId,serverCommand.pmType);
                this.clientDolphin.getClientModelStore().add(presentationModel);
            }
            this.clientDolphin.addAttributeToModel(presentationModel,attribute);
            this.clientDolphin.updateQualifier(presentationModel);
            return presentationModel;
        }
        private handleSavedPresentationModelNotification(serverCommand: spmn.dolphin.SavedPresentationModelNotification){
            if(!serverCommand.pmId) return null;
            var model:cpm.dolphin.ClientPresentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
            if(!model){
                console.log("model with id "+serverCommand.pmId+" not found, cannot rebase.");
                return null;
            }
            model.rebase();
            return model;
        }
        private handlePresentationModelResetedCommand(serverCommand: pmrc.dolphin.PresentationModelResetedCommand): cpm.dolphin.ClientPresentationModel{
            if(!serverCommand.pmId) return null;
            var model:cpm.dolphin.ClientPresentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
            if(!model){
                console.log("model with id "+serverCommand.pmId+" not found, cannot reset.");
                return null;
            }
            model.reset();
            return model;
        }
        //todo: verify the logic
        private handleAttributeMetadataChangedCommand(serverCommand: amdcc.dolphin.AttributeMetadataChangedCommand): cpm.dolphin.ClientPresentationModel{
            var clientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
            if(!clientAttribute) return null;
            clientAttribute[serverCommand.metadataName] = serverCommand.value
            return null;
        }
        private handleCallNamedActionCommand(serverCommand: cna.dolphin.CallNamedActionCommand): cpm.dolphin.ClientPresentationModel{
            this.clientDolphin.send(serverCommand.actionName,null);
            return null;
        }
    }
}