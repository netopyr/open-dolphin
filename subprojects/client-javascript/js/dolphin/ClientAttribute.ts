import cpm = require("../../js/dolphin/ClientPresentationModel")
import bus = require("../../js/dolphin/EventBus")

export module dolphin {

    export interface ValueChangedEvent {
        oldValue;
        newValue;
    }
    var clientAttributeInstanceCount = 0;
    export class ClientAttribute {
        id                : number;
        value             : any;
        // presentationModel : cpm.dolphin.ClientPresentationModel;
        private valueChangeBus : bus.dolphin.EventBus<ValueChangedEvent>;
        private qualifierChangeBus : bus.dolphin.EventBus<ValueChangedEvent>;

        constructor(
            public propertyName  : string,
            public qualifier     : string,
            public tag           : string = "VALUE"
            ) {
            this.id = clientAttributeInstanceCount++;
            this.valueChangeBus = new bus.dolphin.EventBus();
            this.qualifierChangeBus = new bus.dolphin.EventBus();
        }

        setValue(newValue) {
            if (this.value === newValue) return;
            var oldValue = this.value;
            this.value = newValue;
            this.valueChangeBus.trigger( { 'oldValue': oldValue, 'newValue': newValue } );
        }

        setQualifier(newQualifier) {
            if (this.qualifier === newQualifier) return;
            var oldQualifier = this.qualifier;
            this.qualifier = newQualifier;
            this.qualifierChangeBus.trigger({ 'oldValue': oldQualifier, 'newValue': newQualifier });
        }

        // todo:  immediate value update on registration?
        onValueChange(eventHandler: (event : ValueChangedEvent) => void ){
            this.valueChangeBus.onEvent(eventHandler);
        }
        onQualifierChange(eventHandler: (event : ValueChangedEvent) => void ){
            this.qualifierChangeBus.onEvent(eventHandler);
        }
    }
}