import pm = require("../../js/dolphin/ClientPresentationModel");

export module dolphin {

    export interface OnFinishedHandler {
        onFinished(presentationModels:pm.dolphin.ClientPresentationModel[]):void ;
        onFinishedData(data:any[]):void;
    }
}