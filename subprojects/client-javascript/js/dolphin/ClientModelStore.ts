import pm               = require("../../js/dolphin/ClientPresentationModel")
import cd               = require("../../js/dolphin/ClientDolphin")
import cc               = require("../../js/dolphin/ClientConnector")
import createPMCmd      = require("../../js/dolphin/CreatePresentationModelCommand")
import ca               = require("../../js/dolphin/ClientAttribute");
import valueChangedCmd  = require("../../js/dolphin/ValueChangedCommand")
import changeAttMD      = require("../../js/dolphin/ChangeAttributeMetadataCommand")
import attr             = require("../../js/dolphin/Attribute")
import map              = require("../../js/dolphin/Map")
import dpmoftn          = require("../../js/dolphin/DeletedAllPresentationModelsOfTypeNotification")
import bus              = require("../../js/dolphin/EventBus")
import cpm              = require("../../js/dolphin/ClientPresentationModel")
import dpmn             = require("../../js/dolphin/DeletedPresentationModelNotification")
import bvcc             = require("../../js/dolphin/BaseValueChangedCommand")

export module dolphin {

    export enum Type{
        ADDED   = <any> 'ADDED',
        REMOVED = <any> 'REMOVED'
    }
    export interface ModelStoreEvent {
        eventType:Type;
        clientPresentationModel:cpm.dolphin.ClientPresentationModel;
    }

    export class ClientModelStore {

        // the indexes we maintain for fast access
        private presentationModels          : map.dolphin.Map<string,pm.dolphin.ClientPresentationModel>;
        private presentationModelsPerType   : map.dolphin.Map<string,pm.dolphin.ClientPresentationModel[]>;
        private attributesPerId             : map.dolphin.Map<number,ca.dolphin.ClientAttribute>;
        private attributesPerQualifier      : map.dolphin.Map<string,ca.dolphin.ClientAttribute[]>;

        private modelStoreChangeBus         : bus.dolphin.EventBus<ModelStoreEvent>;
        private clientDolphin               : cd.dolphin.ClientDolphin;

        constructor(clientDolphin:cd.dolphin.ClientDolphin) {

            this.clientDolphin = clientDolphin;
            this.presentationModels         = new map.dolphin.Map<string,pm.dolphin.ClientPresentationModel>();
            this.presentationModelsPerType  = new map.dolphin.Map<string,pm.dolphin.ClientPresentationModel[]>();
            this.attributesPerId            = new map.dolphin.Map<number,ca.dolphin.ClientAttribute>();
            this.attributesPerQualifier     = new map.dolphin.Map<string,ca.dolphin.ClientAttribute[]>();
            this.modelStoreChangeBus        = new bus.dolphin.EventBus();
        }

        getClientDolphin() {
            return this.clientDolphin;
        }

        registerModel(model:pm.dolphin.ClientPresentationModel) {
            if (model.clientSideOnly) {
                return;
            }
            var connector:cc.dolphin.ClientConnector = this.clientDolphin.getClientConnector();
            var createPMCommand:createPMCmd.dolphin.CreatePresentationModelCommand = new createPMCmd.dolphin.CreatePresentationModelCommand(model);
            connector.send(createPMCommand, null);
            model.getAttributes().forEach(attribute => { // todo dk: validate. Note that we work on a clone.
                this.registerAttribute(attribute);
            });
        }

        registerAttribute(attribute:ca.dolphin.ClientAttribute) {
            this.addAttributeById(attribute);
            if(attribute.getQualifier()){
                this.addAttributeByQualifier(attribute);
            }
            // whenever an attribute changes its value, the server needs to be notified
            // and all other attributes with the same qualifier are given the same value
            attribute.onValueChange((evt:ca.dolphin.ValueChangedEvent)=> {
                var valueChangeCommand:valueChangedCmd.dolphin.ValueChangedCommand = new valueChangedCmd.dolphin.ValueChangedCommand(attribute.id, evt.oldValue, evt.newValue);
                this.clientDolphin.getClientConnector().send(valueChangeCommand, null);

                if (attribute.getQualifier()) {
                    var attrs = this.findAttributesByFilter((attr:ca.dolphin.ClientAttribute) => {
                        return attr !== attribute && attr.getQualifier() == attribute.getQualifier();
                    })
                    attrs.forEach((attr:ca.dolphin.ClientAttribute) => {
                        attr.setValue(attribute.getValue());
                    })
                }
            });
            // all attributes with the same qualifier should have the same base value
            attribute.onBaseValueChange((evt:ca.dolphin.ValueChangedEvent)=> {
                var baseValueChangeCommand:bvcc.dolphin.BaseValueChangedCommand = new bvcc.dolphin.BaseValueChangedCommand(attribute.id);
                this.clientDolphin.getClientConnector().send(baseValueChangeCommand, null);
                if (attribute.getQualifier()) {
                    var attrs = this.findAttributesByFilter((attr:ca.dolphin.ClientAttribute) => {
                        return attr !== attribute && attr.getQualifier() == attribute.getQualifier();
                    })
                    attrs.forEach((attr:ca.dolphin.ClientAttribute) => {
                        attr.setBaseValue(attribute.getBaseValue());
                    })
                }
            });

            attribute.onQualifierChange((evt:ca.dolphin.ValueChangedEvent)=> {
                var changeAttrMetadataCmd:changeAttMD.dolphin.ChangeAttributeMetadataCommand =
                    new changeAttMD.dolphin.ChangeAttributeMetadataCommand(attribute.id, attr.dolphin.Attribute.QUALIFIER_PROPERTY, evt.newValue);
                this.clientDolphin.getClientConnector().send(changeAttrMetadataCmd, null);
            });

        }
        add(model:pm.dolphin.ClientPresentationModel):boolean {
            if (!model) {
                return false;
            }
            if (this.presentationModels.containsKey(model.id)) {
                console.log("There already is a PM with id " + model.id);
            }
            var added:boolean = false;
            if (!this.presentationModels.containsValue(model)) {
                this.presentationModels.put(model.id, model);
                this.addPresentationModelByType(model);
                this.registerModel(model);

                this.modelStoreChangeBus.trigger({'eventType': Type.ADDED, 'clientPresentationModel': model});
                added = true;
            }
            return added;
        }

        remove(model:pm.dolphin.ClientPresentationModel):boolean {
            if (!model) {
                return false;
            }
            var removed:boolean = false;
            if (this.presentationModels.containsValue(model)) {
                this.removePresentationModelByType(model);
                this.presentationModels.remove(model.id);
                model.getAttributes().forEach((attribute:ca.dolphin.ClientAttribute) => {
                    this.removeAttributeById(attribute);
                    if (attribute.getQualifier()) {
                        this.removeAttributeByQualifier(attribute);
                    }
                })

                this.modelStoreChangeBus.trigger({'eventType': Type.REMOVED, 'clientPresentationModel': model});
                removed = true;
            }
            return removed;
        }

        findAttributesByFilter(filter:(atr:ca.dolphin.ClientAttribute) => boolean) {
            var matches:ca.dolphin.ClientAttribute[] = [];
            this.presentationModels.forEach((key:string, model:pm.dolphin.ClientPresentationModel) => {
                model.getAttributes().forEach((attr) => {
                    if (filter(attr)) {
                        matches.push(attr);
                    }
                })
            })
            return matches;
        }

        addPresentationModelByType(model:pm.dolphin.ClientPresentationModel) {
            if (!model) {
                return;
            }
            var type:string = model.presentationModelType;
            if (!type) {
                return;
            }
            var presentationModels:pm.dolphin.ClientPresentationModel[] = this.presentationModelsPerType.get(type);
            if (!presentationModels) {
                presentationModels = [];
                this.presentationModelsPerType.put(type, presentationModels);
            }
            if (!(presentationModels.indexOf(model) > -1)) {
                presentationModels.push(model);
            }
        }

        removePresentationModelByType(model:pm.dolphin.ClientPresentationModel) {
            if (!model || !(model.presentationModelType)) {
                return;
            }

            var presentationModels:pm.dolphin.ClientPresentationModel[] = this.presentationModelsPerType.get(model.presentationModelType);
            if (!presentationModels) {
                return;
            }
            if (presentationModels.length > -1) {
                presentationModels.splice(presentationModels.indexOf(model), 1);
            }
            if (presentationModels.length === 0) {
                this.presentationModelsPerType.remove(model.presentationModelType);
            }
        }

        listPresentationModelIds():string[] {
            return this.presentationModels.keySet().slice(0);
        }

        listPresentationModels():pm.dolphin.ClientPresentationModel[] {
            return this.presentationModels.values();
        }

        findPresentationModelById(id:string):pm.dolphin.ClientPresentationModel {
            return this.presentationModels.get(id);
        }

        findAllPresentationModelByType(type:string):pm.dolphin.ClientPresentationModel[] {
            if (!type || !this.presentationModelsPerType.containsKey(type)) {
                return [];
            }
            return this.presentationModelsPerType.get(type).slice(0);// slice is used to clone the array
        }

        deleteAllPresentationModelOfType(presentationModelType:string) {
            var presentationModels:pm.dolphin.ClientPresentationModel[] = this.findAllPresentationModelByType(presentationModelType);
            presentationModels.forEach(pm => {
                this.deletePresentationModel(pm, false);
            });
            this.clientDolphin.getClientConnector().send(new dpmoftn.dolphin.DeletedAllPresentationModelsOfTypeNotification(presentationModelType), undefined);
        }

        deletePresentationModel(model:pm.dolphin.ClientPresentationModel, notify:boolean) {
            if (!model) {
                return;
            }
            if (this.containsPresentationModel(model.id)) {
                this.remove(model);
                if (!notify || model.clientSideOnly) {
                    return;
                }
                this.clientDolphin.getClientConnector().send(new dpmn.dolphin.DeletedPresentationModelNotification(model.id), null);
            }
        }

        containsPresentationModel(id:string):boolean {
            return this.presentationModels.containsKey(id);
        }

        addAttributeById(attribute:ca.dolphin.ClientAttribute) {
            if (!attribute || this.attributesPerId.containsKey(attribute.id)) {
                return
            }
            this.attributesPerId.put(attribute.id, attribute);
        }

        removeAttributeById(attribute:ca.dolphin.ClientAttribute) {
            if (!attribute || !this.attributesPerId.containsKey(attribute.id)) {
                return
            }
            this.attributesPerId.remove(attribute.id);
        }

        findAttributeById(id:number):ca.dolphin.ClientAttribute {
            return this.attributesPerId.get(id);
        }

        addAttributeByQualifier(attribute:ca.dolphin.ClientAttribute) {
            if (!attribute || !attribute.getQualifier()) {
                return;
            }
            var attributes:ca.dolphin.ClientAttribute[] = this.attributesPerQualifier.get(attribute.getQualifier());
            if (!attributes) {
                attributes = [];
                this.attributesPerQualifier.put(attribute.getQualifier(), attributes);
            }
            if (!(attributes.indexOf(attribute) > -1)) {
                attributes.push(attribute);
            }

        }

        removeAttributeByQualifier(attribute:ca.dolphin.ClientAttribute) {
            if (!attribute || !attribute.getQualifier()) {
                return;
            }
            var attributes:ca.dolphin.ClientAttribute[] = this.attributesPerQualifier.get(attribute.getQualifier());
            if (!attributes) {
                return;
            }
            if (attributes.length > -1) { // todo dk: check for proper index handling
                attributes.splice(attributes.indexOf(attribute), 1);
            }
            if (attributes.length === 0) {
                this.attributesPerQualifier.remove(attribute.getQualifier());
            }
        }

        findAllAttributeByQualifier(qualifier:string):ca.dolphin.ClientAttribute[] {
            if (!qualifier || !this.attributesPerQualifier.containsKey(qualifier)) {
                return [];
            }
            return this.attributesPerQualifier.get(qualifier).slice(0);// slice is used to clone the array
        }

        onModelStoreChange(eventHandler:(event:ModelStoreEvent) => void) {
            this.modelStoreChangeBus.onEvent(eventHandler);
        }
    }
}