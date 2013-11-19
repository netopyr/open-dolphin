import cpm = require("../../js/dolphin/ClientPresentationModel")

export module dolphin {

    export interface ValueChangedEvent {
        oldValue;
        newValue;
    }

    class EventBus<EventType> {
        private eventHandlers = [];
        onEvent(eventHandler: (event : EventType) => void ) {
            this.eventHandlers.push(eventHandler);
        }
        trigger(event : EventType ) {
            this.eventHandlers.forEach( handle => handle(event));
        }
    }

    var clientAttributeInstanceCount = 0;
    export class ClientAttribute {
        id                : number;
        value             : any;
        presentationModel : cpm.dolphin.ClientPresentationModel;
        private valueChangeBus : EventBus<ValueChangedEvent>;

        constructor(
            public propertyName  : string,
            public qualifier     : string,
            public tag           : string = "VALUE"
            ) {
            this.id = clientAttributeInstanceCount++;
            this.valueChangeBus = new EventBus;
        }

        setValue(newValue) {
            if (this.value === newValue) return;
            var oldValue = this.value;
            this.value = newValue
            this.valueChangeBus.trigger( { 'oldValue': oldValue, 'newValue': newValue } )
        }

        // todo:  immediate value update on registration?
        onValueChange(eventHandler: (event : ValueChangedEvent) => void ){
            this.valueChangeBus.onEvent(eventHandler);
        }
    }
}