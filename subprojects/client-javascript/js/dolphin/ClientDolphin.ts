import namedCmd = require("../../js/dolphin/NamedCommand")
import emptyNot = require("../../js/dolphin/EmptyNotification")
import pm       = require("../../js/dolphin/ClientPresentationModel")
import cms      = require("../../js/dolphin/ClientModelStore")
import cc       = require("../../js/dolphin/ClientConnector")
import ca       = require("../../js/dolphin/ClientAttribute");
import acn      = require("../../js/dolphin/AttributeCreatedNotification")

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
            this.getClientModelStore().deletePresentationModel(modelToDelete, false);
        }

        deleteAllPresentationModelOfType(presentationModelType:string) {
            this.getClientModelStore().deleteAllPresentationModelOfType(presentationModelType);
        }
        updateQualifier(presentationModel:pm.dolphin.ClientPresentationModel):void{
            presentationModel.getAttributes().forEach( sourceAttribute =>{
                if(!sourceAttribute.qualifier) return;
                var attributes = this.getClientModelStore().findAllAttributeByQualifier(sourceAttribute.qualifier);
                attributes.forEach(targetAttribute => {
                    if(targetAttribute.tag != sourceAttribute.tag) return;
                    targetAttribute.setValue(sourceAttribute.getValue());
                });
            });
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
                                                    clientAttribute.qualifier,
                                                    clientAttribute.tag
                                                    ), null);
            }
        }

    }

}