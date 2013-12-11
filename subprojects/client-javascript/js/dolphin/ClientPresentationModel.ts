import ca  = require("../../js/dolphin/ClientAttribute");
import bus = require("../../js/dolphin/EventBus")


export module dolphin {

    export interface InvalidationEvent {
        source: ClientPresentationModel;
    }
    var presentationModelInstanceCount = 0;

    export class ClientPresentationModel {

        attributes:ca.dolphin.ClientAttribute[] = [];
        isClientSideOnly:boolean = false;
        private dirty:boolean = false;
        private invalidBus:bus.dolphin.EventBus<InvalidationEvent>;
        private dirtyValueChangeBus:bus.dolphin.EventBus<ca.dolphin.ValueChangedEvent>;

        constructor(public id:string, public presentationModelType:string) {
            if (typeof id !== 'undefined') { // even an empty string is a valid id
                this.id = id;
            } else {
                this.id = (presentationModelInstanceCount++).toString();
            }
            this.invalidBus = new bus.dolphin.EventBus();
            this.dirtyValueChangeBus = new bus.dolphin.EventBus();
        }

        addAttribute(attribute:ca.dolphin.ClientAttribute) {
            if(!attribute || (this.attributes.indexOf(attribute)>-1)){
                return;
            }
            if(this.findAttributeByPropertyNameAndTag(attribute.propertyName,attribute.tag)){
                throw new Error("There already is an attribute with property name: " + attribute.propertyName
                    +" and tag: "+attribute.tag + " in presentation model with id: "+ this.id);
            }
            if(attribute.qualifier && this.findAttributeByQualifier(attribute.qualifier)){
                throw new Error("There already is an attribute with qualifier: " + attribute.qualifier
                    +" in presentation model with id: "+ this.id);
            }
            //attribute.setPresentationModel(this);
            this.attributes.push(attribute);
            this.updateDirty(); //todo : check for TAG

            attribute.onValueChange((evt:ca.dolphin.ValueChangedEvent)=> {
                this.invalidBus.trigger({source: this});
            });
        }

        updateDirty(){
            this.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                if(attribute.isDirty()){
                    this.setDirty(true);
                    return;
                }
            });
            this.setDirty(false);
        }
        isDirty(): boolean{
            return this.dirty;
        }

        setDirty(dirty:boolean){
            var oldVal = this.dirty;
            this.dirty = dirty;
            this.dirtyValueChangeBus.trigger({ 'oldValue': oldVal, 'newValue': this.dirty });
        }

        reset(): void{
            this.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                attribute.reset();
            });
        }

        rebase(): void{
            this.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                attribute.rebase();
            });
        }

        onDirty(eventHandler:(event:ca.dolphin.ValueChangedEvent) => void) {
            this.dirtyValueChangeBus.onEvent(eventHandler);
        }
        onInvalidated(handleInvalidate:(InvalidationEvent) => void) {
            this.invalidBus.onEvent(handleInvalidate);
        }

        // todo:is this method needed
        getAttributes(): ca.dolphin.ClientAttribute[]{
            return this.attributes.slice(0);
        }
        getAt(propertyName:string, tag:string):ca.dolphin.ClientAttribute{
            return this.findAttributeByPropertyNameAndTag(propertyName, tag);
        }

        findAttributeByPropertyName(propertyName: string): ca.dolphin.ClientAttribute{
            return this.findAttributeByPropertyNameAndTag(propertyName, "VALUE");
        }

        findAllAttributesByPropertyName(propertyName: string): ca.dolphin.ClientAttribute[]{
            var result:ca.dolphin.ClientAttribute[] = [];
            if(!propertyName) return null;
            this.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                if(attribute.propertyName == propertyName){
                    result.push(attribute);
                }
            });
            return result;
        }

        findAttributeByPropertyNameAndTag(propertyName:string, tag:string): ca.dolphin.ClientAttribute{
            if(!propertyName || !tag) return null;
            this.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                if((attribute.propertyName == propertyName) && (attribute.tag == tag)){
                    return attribute;
                }
            });
            return null;
        }
        findAttributeByQualifier(qualifier:string): ca.dolphin.ClientAttribute{
            if(!qualifier) return null;
            this.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                if(attribute.qualifier == qualifier){
                      return attribute;
                }
            });
            return null;
        }

        findAttributeById(id:number): ca.dolphin.ClientAttribute{
            if(!id) return null;
            this.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                if(attribute.id == id){
                    return attribute;
                }
            });
            return null;
        }

        syncWith(sourcePresentationModel: ClientPresentationModel): void{
            this.attributes.forEach((targetAttribute:ca.dolphin.ClientAttribute) => {
                var sourceAttribute = sourcePresentationModel.getAt(targetAttribute.propertyName,targetAttribute.tag);
                if(sourceAttribute){
                    targetAttribute.syncWith(sourceAttribute);
                }
            });
        }

    }
}