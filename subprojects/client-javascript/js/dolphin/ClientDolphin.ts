import namedCmd = require("../../js/dolphin/NamedCommand")
import emptyNot = require("../../js/dolphin/EmptyNotification")
import pm       = require("../../js/dolphin/ClientPresentationModel")
import cms      = require("../../js/dolphin/ClientModelStore")
import cc       = require("../../js/dolphin/ClientConnector")
import ca       = require("../../js/dolphin/ClientAttribute");
import dol      = require("../../js/dolphin/Dolphin")

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

        delete(modelToDelete:pm.dolphin.ClientPresentationModel) {
            this.getClientModelStore().delete(modelToDelete, false);
        }

        deleteAllPresentationModelOfType(presentationModelType:string) {
            this.getClientModelStore().deleteAllPresentationModelOfType(presentationModelType);
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


    }

}