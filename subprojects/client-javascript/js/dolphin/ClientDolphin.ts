import namedCmd = require("../../js/dolphin/NamedCommand")
import emptyNot = require("../../js/dolphin/EmptyNotification")
import pm       = require("../../js/dolphin/ClientPresentationModel")
import cms      = require("../../js/dolphin/ClientModelStore")
import cc       = require("../../js/dolphin/ClientConnector")
import ca       = require("../../js/dolphin/ClientAttribute");
import dol      = require("../../js/dolphin/Dolphin")
import acn      = require("../../js/dolphin/AttributeCreatedNotification")

export module dolphin {

    export class ClientDolphin extends dol.dolphin.Dolphin {

        private clientConnector:cc.dolphin.ClientConnector;

        setClientConnector(clientConnector:cc.dolphin.ClientConnector) {
            this.clientConnector = clientConnector;
        }

        getClientConnector():cc.dolphin.ClientConnector {
            return this.clientConnector;
        }

        send(commandName:string, onFinished:cc.dolphin.OnFinishedAdapter) {
            this.clientConnector.send(new namedCmd.dolphin.NamedCommand(commandName), onFinished);
        }

        sendEmpty(onFinished:cc.dolphin.OnFinishedAdapter) {
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

        addAttributeToModel(presentationModel:pm.dolphin.ClientPresentationModel, clientAttribute: ca.dolphin.ClientAttribute){
            presentationModel.addAttribute(clientAttribute);
            //todo: clientModelStore.registerAttribute
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