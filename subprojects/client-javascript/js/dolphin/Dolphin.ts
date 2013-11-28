import cms      = require("../../js/dolphin/ClientModelStore")
import pm               = require("../../js/dolphin/ClientPresentationModel")
export module dolphin {
    export class Dolphin {
        private clientModelStore:cms.dolphin.ClientModelStore;

        setClientModelStore(clientModelStore:cms.dolphin.ClientModelStore) {
            this.clientModelStore = clientModelStore;
        }

        getClientModelStore():cms.dolphin.ClientModelStore {
            return this.clientModelStore;
        }

        listPresentationModelIds() {
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


    }
}