import cpm = require("../../js/dolphin/ClientPresentationModel")
import bus = require("../../js/dolphin/EventBus")

export module dolphin {


    export interface ValueChangedEvent {
        oldValue;
        newValue;
    }
    var clientAttributeInstanceCount = 0;
    export class ClientAttribute {
        private static SUPPORTED_VALUE_TYPES:string[] = ["string", "number", "boolean"];
        id:number;
        value:any;
        private dirty:boolean = false;
        private baseValue:any
        private presentationModel:cpm.dolphin.ClientPresentationModel;
        private valueChangeBus:bus.dolphin.EventBus<ValueChangedEvent>;
        private qualifierChangeBus:bus.dolphin.EventBus<ValueChangedEvent>;
        private dirtyValueChangeBus:bus.dolphin.EventBus<ValueChangedEvent>;
        private baseValueChangeBus:bus.dolphin.EventBus<ValueChangedEvent>;

        constructor(public propertyName:string, public qualifier:string, public tag:string = "VALUE") {
            this.id = clientAttributeInstanceCount++;
            this.valueChangeBus = new bus.dolphin.EventBus();
            this.qualifierChangeBus = new bus.dolphin.EventBus();
            this.dirtyValueChangeBus = new bus.dolphin.EventBus();
            this.baseValueChangeBus = new bus.dolphin.EventBus();
        }

        isDirty():boolean {
            return this.dirty;
        }

        getBaseValue() {
            return this.baseValue;
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
            var verifiedValue = ClientAttribute.checkValue(newValue);
            if (this.value === verifiedValue) return;
            if (this.baseValue == null) {
                this.setDirty(verifiedValue != null)
            } else {
                this.setDirty(this.baseValue != verifiedValue)
            }
            var oldValue = this.value;
            this.value = verifiedValue;
            this.valueChangeBus.trigger({ 'oldValue': oldValue, 'newValue': verifiedValue });
        }

        private setDirty(dirty:boolean) {
            var oldVal = this.dirty;
            this.dirty = dirty;
            this.dirtyValueChangeBus.trigger({ 'oldValue': oldVal, 'newValue': this.dirty });
        }

        setQualifier(newQualifier) {
            if (this.qualifier === newQualifier) return;
            var oldQualifier = this.qualifier;
            this.qualifier = newQualifier;
            this.qualifierChangeBus.trigger({ 'oldValue': oldQualifier, 'newValue': newQualifier });
        }

        private setBaseValue(baseValue:any) {

            if (this.baseValue === baseValue) return;
            if (this.baseValue == null) {
                this.setDirty(this.value != null);
            } else {
                this.setDirty(baseValue != this.value)
            }
            var oldBaseValue = this.baseValue;
            this.baseValue = baseValue;
            this.baseValueChangeBus.trigger({ 'oldValue': oldBaseValue, 'newValue': baseValue });
        }

        rebase() {
            this.setBaseValue(this.value);
        }

        reset() {
            this.setValue(this.baseValue);
            this.setDirty(false);
        }

        static checkValue(value:any) : any {
            if (value == null || value == undefined) {
                return null;
            }
            var result = value;
            if (result instanceof String || result instanceof Boolean || result instanceof Number) {
                result = value.valueOf();
            }
            if (result instanceof ClientAttribute) {
                console.log("An Attribute may not itself contain an attribute as a value. Assuming you forgot to call value.")
                result = this.checkValue((<ClientAttribute>value).value);
            }
            var ok:boolean = false;
            if (this.SUPPORTED_VALUE_TYPES.indexOf(typeof result) > -1 || result instanceof Date) {
                ok = true;
            }
            if (!ok) {
                throw new Error("Attribute values of this type are not allowed: " + typeof value);
            }
            return result;

        }

        // todo:  immediate value update on registration?
        onValueChange(eventHandler:(event:ValueChangedEvent) => void) {
            this.valueChangeBus.onEvent(eventHandler);
        }

        onQualifierChange(eventHandler:(event:ValueChangedEvent) => void) {
            this.qualifierChangeBus.onEvent(eventHandler);
        }

        onDirty(eventHandler:(event:ValueChangedEvent) => void) {
            this.dirtyValueChangeBus.onEvent(eventHandler);
        }

        onBaseValueChange(eventHandler:(event:ValueChangedEvent) => void) {
            this.baseValueChangeBus.onEvent(eventHandler);
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