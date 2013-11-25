import ofn = require("../../js/dolphin/OnFinishedHandler");
import pm  = require("../../js/dolphin/ClientPresentationModel");
export module dolphin {

    export class OnFinishedHandlerAdapter implements ofn.dolphin.OnFinishedHandler {
        onFinished(presentationModels:pm.dolphin.ClientPresentationModel[]):void {
        }

        onFinishedData(data:any[]) {

        }

    }
}