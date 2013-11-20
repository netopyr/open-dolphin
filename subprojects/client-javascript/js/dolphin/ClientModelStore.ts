import pm = require("../../js/dolphin/ClientPresentationModel")

export module dolphin {

    export class ClientModelStore {

        models : pm.dolphin.ClientPresentationModel[] = [];

        add(model:pm.dolphin.ClientPresentationModel){
            this.models.push(model);
        }
    }
}