import cpm    = require("../../js/dolphin/ClientPresentationModel");
import cmd    = require("../../js/dolphin/Command");
import cb     = require("../../js/dolphin/CommandBatcher");
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
import dcmd   = require("../../js/dolphin/DataCommand");
import ncmd   = require("../../js/dolphin/NamedCommand");
import scmd   = require("../../js/dolphin/SignalCommand");
import tags   = require("../../js/dolphin/Tag");

export module dolphin {

    export interface OnFinishedHandler {
        onFinished(models:cpm.dolphin.ClientPresentationModel[]):void
        onFinishedData(listOfData:any[]):void
    }

    export interface CommandAndHandler {
        command : cmd.dolphin.Command;
        handler : OnFinishedHandler;
    }

    export interface Transmitter {
        transmit(commands:cmd.dolphin.Command[], onDone:(result:cmd.dolphin.Command[]) => void) : void ;
        signal(command:scmd.dolphin.SignalCommand) : void;
    }

    export class ClientConnector {

        private commandQueue :      CommandAndHandler[] = [];
        private currentlySending :  boolean = false;
        private slackMS:            number; // slack milliseconds for rendering and batching
        private transmitter :       Transmitter;
        private codec :             cod.dolphin.Codec;
        private clientDolphin :     cd.dolphin.ClientDolphin;
        private commandBatcher:     cb.dolphin.CommandBatcher = new cb.dolphin.BlindCommandBatcher(true);

        /////// push support state  ///////
        private pushListener:       ncmd.dolphin.NamedCommand;
        private releaseCommand:     scmd.dolphin.SignalCommand;
        private pushEnabled:        boolean = false;
        private waiting:            boolean = false;


        constructor(transmitter:Transmitter, clientDolphin:cd.dolphin.ClientDolphin, slackMS: number = 0) {
            this.transmitter = transmitter;
            this.clientDolphin = clientDolphin;
            this.slackMS = slackMS;
            this.codec = new cod.dolphin.Codec();
        }

        setCommandBatcher(newBatcher: cb.dolphin.CommandBatcher) {
            this.commandBatcher = newBatcher;
        }
        setPushEnabled(enabled:boolean) {
            this.pushEnabled = enabled;
        }
        setPushListener(newListener: ncmd.dolphin.NamedCommand) {
            this.pushListener = newListener
        }
        setReleaseCommand(newCommand: scmd.dolphin.SignalCommand) {
            this.releaseCommand = newCommand
        }

        send(command:cmd.dolphin.Command, onFinished:OnFinishedHandler) {
            this.commandQueue.push({command: command, handler: onFinished });
            if (this.currentlySending) {
                if(command != this.pushListener) this.release(); // there is not point in releasing if we do not send atm
                return;
            }
            this.doSendNext();
        }

        private doSendNext() {
            if (this.commandQueue.length < 1) {
                this.currentlySending = false;
                return;
            }
            this.currentlySending = true;

            var cmdsAndHandlers = this.commandBatcher.batch(this.commandQueue);
            var callback = cmdsAndHandlers[cmdsAndHandlers.length-1].handler;
            var commands = cmdsAndHandlers.map( cah => { return cah.command });
            this.transmitter.transmit(commands, (response:cmd.dolphin.Command[]) => {

                //console.log("server response: [" + response.map(it => it.id).join(", ") + "] ");

                var touchedPMs : cpm.dolphin.ClientPresentationModel[] = []
                response.forEach((command:cmd.dolphin.Command) => {
                    var touched = this.handle(command);
                    if (touched) touchedPMs.push(touched);
                });

                if (callback) {
                    callback.onFinished(touchedPMs); // todo: make them unique?
                    // todo dk: handling of data from datacommand
                }

                // recursive call: fetch the next in line but allow a bit of slack such that
                // document events can fire, rendering is done and commands can batch up
                setTimeout( () => this.doSendNext() , this.slackMS );
            });
        }



        handle(command:cmd.dolphin.Command): cpm.dolphin.ClientPresentationModel{
            if(command.id == "Data"){
                return this.handleDataCommand(<dcmd.dolphin.DataCommand>command);
            }else if(command.id == "DeletePresentationModel"){
                return this.handleDeletePresentationModelCommand(<dpmc.dolphin.DeletePresentationModelCommand>command);
            }else if(command.id == "DeleteAllPresentationModelsOfType"){
                return this.handleDeleteAllPresentationModelOfTypeCommand(<dapmc.dolphin.DeleteAllPresentationModelsOfTypeCommand>command);
            }else if(command.id == "CreatePresentationModel"){
                return this.handleCreatePresentationModelCommand(<cpmc.dolphin.CreatePresentationModelCommand>command);
            }else if(command.id == "ValueChanged"){
                return this.handleValueChangedCommand(<vcc.dolphin.ValueChangedCommand>command);
            }else if(command.id == "BaseValueChanged"){
                return this.handleBaseValueChangedCommand(<bvcc.dolphin.BaseValueChangedCommand>command);
            }else if(command.id == "SwitchPresentationModel"){
                return this.handleSwitchPresentationModelCommand(<spmc.dolphin.SwitchPresentationModelCommand>command);
            }else if(command.id == "InitializeAttribute"){
                return this.handleInitializeAttributeCommand(<iac.dolphin.InitializeAttributeCommand>command);
            }else if(command.id == "SavedPresentationModel"){
                return this.handleSavedPresentationModelNotification(<spmn.dolphin.SavedPresentationModelNotification>command);
            }else if(command.id == "PresentationModelReseted"){
                return this.handlePresentationModelResetedCommand(<pmrc.dolphin.PresentationModelResetedCommand>command);
            }else if(command.id == "AttributeMetadataChanged"){
                return this.handleAttributeMetadataChangedCommand(<amdcc.dolphin.AttributeMetadataChangedCommand>command);
            }else if(command.id == "CallNamedAction"){
                return this.handleCallNamedActionCommand(<cna.dolphin.CallNamedActionCommand>command);
            }else{
                console.log("Cannot handle, unknown command "+command);
            }

            return null;
        }
        private handleDataCommand(serverCommand: dcmd.dolphin.DataCommand): any{
            return serverCommand.data;
        }
        private handleDeletePresentationModelCommand(serverCommand:dpmc.dolphin.DeletePresentationModelCommand):cpm.dolphin.ClientPresentationModel{
            var model:cpm.dolphin.ClientPresentationModel =  this.clientDolphin.findPresentationModelById(serverCommand.pmId);
            if(!model) return null;
            this.clientDolphin.getClientModelStore().deletePresentationModel(model, true);
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
                var clientAttribute = this.clientDolphin.attribute(attr.propertyName,attr.qualifier,attr.value, attr.tag ? attr.tag : tags.dolphin.Tag.value());
                clientAttribute.setBaseValue(attr.baseValue);
                attributes.push(clientAttribute);
            });
            var clientPm = new cpm.dolphin.ClientPresentationModel(serverCommand.pmId, serverCommand.pmType);
            clientPm.addAttributes(attributes);
            if(serverCommand.clientSideOnly){
                clientPm.clientSideOnly = true;
            }
            this.clientDolphin.getClientModelStore().add(clientPm);
            this.clientDolphin.updateQualifier(clientPm);
            clientPm.updateAttributeDirtyness();
            clientPm.updateDirty();
            return clientPm;
        }
        private handleValueChangedCommand(serverCommand:vcc.dolphin.ValueChangedCommand):cpm.dolphin.ClientPresentationModel{
            var clientAttribute: ca.dolphin.ClientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
            if(!clientAttribute){
                console.log("attribute with id "+serverCommand.attributeId+" not found, cannot update old value "+serverCommand.oldValue+" to new value "+serverCommand.newValue);
                return null;
            }
            clientAttribute.setValue(serverCommand.newValue);
            return null;
        }
        private handleBaseValueChangedCommand(serverCommand:bvcc.dolphin.BaseValueChangedCommand):cpm.dolphin.ClientPresentationModel{
            var clientAttribute: ca.dolphin.ClientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
            if(!clientAttribute){
                console.log("attribute with id "+serverCommand.attributeId+" not found, cannot set base value.");
                return null;
            }
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


        ///////////// push support ///////////////

        listen() : void {
            if (! this.pushEnabled) return;
            if (this.waiting) return;
            // todo: how to issue a warning if no pushListener is set?
            this.waiting = true;
            var me = this; // oh, boy, this took some time to find...
            this.send(this.pushListener, { onFinished: function(models) {
                me.waiting = false;
                me.listen();
            }, onFinishedData: null})
        }

        release() : void {
            if (! this.waiting) return;
            this.waiting = false;
            // todo: how to issue a warning if no releaseCommand is set?
            this.transmitter.signal(this.releaseCommand);
        }

    }
}