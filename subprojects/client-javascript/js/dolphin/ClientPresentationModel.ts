import ca  = require("../../js/dolphin/ClientAttribute");
import bus = require("../../js/dolphin/EventBus")

export module dolphin {

    export interface InvalidationEvent {
        source: ClientPresentationModel;
    }
    var presentationModelInstanceCount = 0;

    export class ClientPresentationModel {

        attributes:ca.dolphin.ClientAttribute[] = [];
        private invalidBus:bus.dolphin.EventBus<InvalidationEvent>;
        isClientSideOnly:boolean = false;

        constructor(public id:string, public presentationModelType:string) {
            if (typeof id !== 'undefined') { // even an empty string is a valid id
                this.id = id;
            } else {
                this.id = (presentationModelInstanceCount++).toString();
            }
            this.invalidBus = new bus.dolphin.EventBus();
        }

        addAttribute(attribute:ca.dolphin.ClientAttribute) {
            this.attributes.push(attribute);
            attribute.onValueChange((evt:ca.dolphin.ValueChangedEvent)=> {
                this.invalidBus.trigger({source: this});
            });
        }

        onInvalidated(handleInvalidate:(InvalidationEvent) => void) {
            this.invalidBus.onEvent(handleInvalidate);
        }

    }
}