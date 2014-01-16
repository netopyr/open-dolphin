import ca   = require("../../js/dolphin/ClientAttribute");
import bus  = require("../../js/dolphin/EventBus")
import tags = require("../../js/dolphin/Tag")

export module dolphin {

    export interface InvalidationEvent {
        source: ClientPresentationModel;
    }
    var presentationModelInstanceCount = 0; // todo dk: consider making this static in class

    export class ClientPresentationModel {

        private attributes:ca.dolphin.ClientAttribute[] = [];
        clientSideOnly:boolean = false;
        private dirty:boolean = false;
        private invalidBus:bus.dolphin.EventBus<InvalidationEvent>;
        private dirtyValueChangeBus:bus.dolphin.EventBus<ca.dolphin.ValueChangedEvent>;

        constructor(public id:string, public presentationModelType:string) {
            if (typeof id !== 'undefined' && id != null) { // even an empty string is a valid id
                this.id = id;
            } else {
                this.id = (presentationModelInstanceCount++).toString();
            }
            this.invalidBus = new bus.dolphin.EventBus();
            this.dirtyValueChangeBus = new bus.dolphin.EventBus();
        }

        /** a copy constructor for anything but IDs. Per default, copies are client side only, no automatic update applies. */
        copy() {
            var result = new ClientPresentationModel(null, this.presentationModelType);
            result.clientSideOnly = true;
            this.getAttributes().forEach( (attribute: ca.dolphin.ClientAttribute) => {
                var attributeCopy = attribute.copy();
                result.addAttribute(attributeCopy);
            });
            return result;
        }

        //add array of attributes
        addAttributes(attributes:ca.dolphin.ClientAttribute[]){
            if(!attributes || attributes.length < 1) return;
            attributes.forEach(attr => {
                this.addAttribute(attr);
            });
        }
        addAttribute(attribute:ca.dolphin.ClientAttribute) {
            if(!attribute || (this.attributes.indexOf(attribute)>-1)){
                return;
            }
            if(this.findAttributeByPropertyNameAndTag(attribute.propertyName,attribute.tag)){
                throw new Error("There already is an attribute with property name: " + attribute.propertyName
                    +" and tag: "+attribute.tag + " in presentation model with id: "+ this.id);
            }
            if(attribute.getQualifier() && this.findAttributeByQualifier(attribute.getQualifier())){
                throw new Error("There already is an attribute with qualifier: " + attribute.getQualifier()
                    +" in presentation model with id: "+ this.id);
            }
            attribute.setPresentationModel(this);
            this.attributes.push(attribute);
            if(attribute.tag == tags.dolphin.Tag.value()){ // the consideration here is that only VALUE changes can make a PM dirty. TODO: consistency check with Java client.
                this.updateDirty();
            }
            attribute.onValueChange((evt:ca.dolphin.ValueChangedEvent)=> {
                this.invalidBus.trigger({source: this});
            });
        }

        updateDirty(){
            for(var i=0;i<this.attributes.length;i++){
                if(this.attributes[i].isDirty()){
                    this.setDirty(true);
                    return;
                }
            };
            this.setDirty(false);
        }

        updateAttributeDirtyness(){
            for(var i=0;i<this.attributes.length;i++){
                this.attributes[i].updateDirty();
            }
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

        /** returns a copy of the internal state */
        getAttributes(): ca.dolphin.ClientAttribute[]{
            return this.attributes.slice(0);
        }
        getAt(propertyName:string, tag:string = tags.dolphin.Tag.value()):ca.dolphin.ClientAttribute{
            return this.findAttributeByPropertyNameAndTag(propertyName, tag);
        }

        findAttributeByPropertyName(propertyName: string): ca.dolphin.ClientAttribute{
            return this.findAttributeByPropertyNameAndTag(propertyName, tags.dolphin.Tag.value());
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
            for(var i=0;i<this.attributes.length;i++){
                if((this.attributes[i].propertyName == propertyName) && (this.attributes[i].tag == tag)){
                    return this.attributes[i];
                }
            }
            return null;
        }
        findAttributeByQualifier(qualifier:string): ca.dolphin.ClientAttribute{
            if(!qualifier) return null;
            for(var i=0;i<this.attributes.length;i++){
                if(this.attributes[i].getQualifier() == qualifier){
                    return this.attributes[i];
                }
            };
            return null;
        }

        findAttributeById(id:number): ca.dolphin.ClientAttribute{
            if(!id) return null;
            for(var i=0;i<this.attributes.length;i++){
                if(this.attributes[i].id == id){
                    return this.attributes[i];
                }
            };
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