import namedCmd = require("../../js/dolphin/NamedCommand")
import emptyNot = require("../../js/dolphin/EmptyNotification")
import pm       = require("../../js/dolphin/ClientPresentationModel")
import cms      = require("../../js/dolphin/ClientModelStore")
import cc       = require("../../js/dolphin/ClientConnector")
import ca       = require("../../js/dolphin/ClientAttribute");

export module dolphin {

    export class ClientDolphin {

        private clientModelStore:cms.dolphin.ClientModelStore;
        private clientConnector:cc.dolphin.ClientConnector;

        setClientModelStore(clientModelStore:cms.dolphin.ClientModelStore) {
            this.clientModelStore = clientModelStore;
        }

        getClientModelStore() {
            return this.clientModelStore;
        }

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
            this.clientModelStore.add(model);
            return model;
        }
    }

}