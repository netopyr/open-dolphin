import namedCmd = require("../../js/dolphin/NamedCommand");
import signlCmd = require("../../js/dolphin/SignalCommand");
import emptyNot = require("../../js/dolphin/EmptyNotification");
import pm       = require("../../js/dolphin/ClientPresentationModel");
import cms      = require("../../js/dolphin/ClientModelStore");
import cc       = require("../../js/dolphin/ClientConnector");
import ca       = require("../../js/dolphin/ClientAttribute");
import acn      = require("../../js/dolphin/AttributeCreatedNotification");

export module dolphin {

    export class ClientDolphin {


        private clientConnector:cc.dolphin.ClientConnector;
        private clientModelStore:cms.dolphin.ClientModelStore;

        setClientConnector(clientConnector:cc.dolphin.ClientConnector) {
            this.clientConnector = clientConnector;
        }

        getClientConnector():cc.dolphin.ClientConnector {
            return this.clientConnector;
        }

        send(commandName:string, onFinished:cc.dolphin.OnFinishedHandler) {
            this.clientConnector.send(new namedCmd.dolphin.NamedCommand(commandName), onFinished);
        }

        sendEmpty(onFinished:cc.dolphin.OnFinishedHandler) {
            this.clientConnector.send(new emptyNot.dolphin.EmptyNotification(), onFinished);
        }

        // factory method for attributes
        attribute(propertyName, qualifier, value, tag) {
            return new ca.dolphin.ClientAttribute(propertyName, qualifier, value, tag);
        }

        // factory method for presentation models
        presentationModel(id:string, type:string, ...attributes:ca.dolphin.ClientAttribute[]) {
            var model:pm.dolphin.ClientPresentationModel = new pm.dolphin.ClientPresentationModel(id, type);
            if (attributes && attributes.length > 0) {
                attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                    model.addAttribute(attribute);
                });
            }
            this.getClientModelStore().add(model);
            return model;
        }

        setClientModelStore(clientModelStore:cms.dolphin.ClientModelStore) {
            this.clientModelStore = clientModelStore;
        }

        getClientModelStore():cms.dolphin.ClientModelStore {
            return this.clientModelStore;
        }

        listPresentationModelIds():string[] {
            return this.getClientModelStore().listPresentationModelIds();
        }

        listPresentationModels(): pm.dolphin.ClientPresentationModel[]{
            return this.getClientModelStore().listPresentationModels();
        }

        findAllPresentationModelByType(presentationModelType:string):pm.dolphin.ClientPresentationModel[] {
            return this.getClientModelStore().findAllPresentationModelByType(presentationModelType);
        }

        getAt(id:string):pm.dolphin.ClientPresentationModel {
            return this.findPresentationModelById(id);
        }

        findPresentationModelById(id:string):pm.dolphin.ClientPresentationModel {
            return this.getClientModelStore().findPresentationModelById(id);
        }
        deletePresentationModel(modelToDelete:pm.dolphin.ClientPresentationModel) {
            this.getClientModelStore().deletePresentationModel(modelToDelete, true);
        }

        deleteAllPresentationModelOfType(presentationModelType:string) {
            this.getClientModelStore().deleteAllPresentationModelOfType(presentationModelType);
        }
        updateQualifier(presentationModel:pm.dolphin.ClientPresentationModel):void{
            presentationModel.getAttributes().forEach( sourceAttribute =>{
                if(!sourceAttribute.getQualifier()) return;
                var attributes = this.getClientModelStore().findAllAttributeByQualifier(sourceAttribute.getQualifier());
                attributes.forEach(targetAttribute => {
                    if(targetAttribute.tag != sourceAttribute.tag) return;
                    targetAttribute.setValue(sourceAttribute.getValue());
                });
            });
        }

        tag(presentationModel: pm.dolphin.ClientPresentationModel,propertyName:string,value:any, tag:string):  ca.dolphin.ClientAttribute{
            var clientAttribute: ca.dolphin.ClientAttribute = new ca.dolphin.ClientAttribute(propertyName, null, value, tag);
            this.addAttributeToModel(presentationModel, clientAttribute);
            return clientAttribute;
        }

        addAttributeToModel(presentationModel:pm.dolphin.ClientPresentationModel, clientAttribute: ca.dolphin.ClientAttribute){
            presentationModel.addAttribute(clientAttribute);
            this.getClientModelStore().registerAttribute(clientAttribute);
            if(!presentationModel.clientSideOnly){
                this.clientConnector.send(new acn.dolphin.AttributeCreatedNotification(
                                                    presentationModel.id,
                                                    clientAttribute.id,
                                                    clientAttribute.propertyName,
                                                    clientAttribute.getValue(),
                                                    clientAttribute.getQualifier(),
                                                    clientAttribute.tag
                                                    ), null);
            }
        }

        ////// push support ///////
        startPushListening(pushActionName: string, releaseActionName: string) {
            this.clientConnector.setPushListener(new namedCmd.dolphin.NamedCommand(pushActionName));
            this.clientConnector.setReleaseCommand(new signlCmd.dolphin.SignalCommand(releaseActionName));
            this.clientConnector.setPushEnabled(true);
            this.clientConnector.listen();
        }
        stopPushListening() {
            this.clientConnector.setPushEnabled(false);
        }

    }

}