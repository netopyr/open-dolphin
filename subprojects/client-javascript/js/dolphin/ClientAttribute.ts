import cpm = require("../../js/dolphin/ClientPresentationModel")
import bus = require("../../js/dolphin/EventBus")

export module dolphin {

    export interface ValueChangedEvent {
        oldValue;
        newValue;
    }
    var clientAttributeInstanceCount = 0;
    export class ClientAttribute {
        id:number;
        value:any;
        private dirty:boolean
        private baseValue:any
        private presentationModel:cpm.dolphin.ClientPresentationModel;
        private valueChangeBus:bus.dolphin.EventBus<ValueChangedEvent>;
        private qualifierChangeBus:bus.dolphin.EventBus<ValueChangedEvent>;

        constructor(public propertyName:string, public qualifier:string, public tag:string = "VALUE") {
            this.id = clientAttributeInstanceCount++;
            this.valueChangeBus = new bus.dolphin.EventBus();
            this.qualifierChangeBus = new bus.dolphin.EventBus();
        }

        isDirty():boolean {
            return this.dirty;
        }

        getBaseValue() {
            return this.baseValue;
        }

        setBaseValue(baseValue:any) {
            //todo- to be implemented
        }

        setPresentationModel(presentationModel:cpm.dolphin.ClientPresentationModel) {
            if (this.presentationModel) {
                alert("You can not set a presentation model for an attribute that is already bound.");
            }
            this.presentationModel = presentationModel;
        }

        getPresentationModel():cpm.dolphin.ClientPresentationModel {
            return this.presentationModel;
        }

        setValue(newValue) {
            if (this.value === newValue) return;
            var oldValue = this.value;
            this.value = newValue;
            this.valueChangeBus.trigger({ 'oldValue': oldValue, 'newValue': newValue });
        }

        setQualifier(newQualifier) {
            if (this.qualifier === newQualifier) return;
            var oldQualifier = this.qualifier;
            this.qualifier = newQualifier;
            this.qualifierChangeBus.trigger({ 'oldValue': oldQualifier, 'newValue': newQualifier });
        }

        // todo:  immediate value update on registration?
        onValueChange(eventHandler:(event:ValueChangedEvent) => void) {
            this.valueChangeBus.onEvent(eventHandler);
        }

        onQualifierChange(eventHandler:(event:ValueChangedEvent) => void) {
            this.qualifierChangeBus.onEvent(eventHandler);
        }

        syncWith(sourceAttribute:ClientAttribute) {
            if (sourceAttribute) {
                this.setBaseValue(sourceAttribute.getBaseValue());
                this.setQualifier(sourceAttribute.qualifier);
                this.setValue(sourceAttribute.value);
            }
        }
    }
}