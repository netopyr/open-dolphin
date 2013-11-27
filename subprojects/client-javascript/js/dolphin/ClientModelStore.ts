import pm               = require("../../js/dolphin/ClientPresentationModel")
import cd               = require("../../js/dolphin/ClientDolphin")
import cc               = require("../../js/dolphin/ClientConnector")
import createPMCmd      = require("../../js/dolphin/CreatePresentationModelCommand")
import ca               = require("../../js/dolphin/ClientAttribute");
import valueChangedCmd  = require("../../js/dolphin/ValueChangedCommand")
import changeAttMD      = require("../../js/dolphin/ChangeAttributeMetadataCommand")
import attr             = require("../../js/dolphin/Attribute")
import map              = require("../../js/dolphin/Map")
import dpmoftn          = require("../../js/dolphin/DeleteAllPresentationModelsOfTypeNotification")

export module dolphin {

    export class ClientModelStore {

        private presentationModels:map.dolphin.Map<string,pm.dolphin.ClientPresentationModel>;
        private presentationModelsPerType:map.dolphin.Map<string,pm.dolphin.ClientPresentationModel[]>;
        private attributesPerId:map.dolphin.Map<number,ca.dolphin.ClientAttribute>;
        private attributesPerQualifier:map.dolphin.Map<string,ca.dolphin.ClientAttribute[]>;


        private clientDolphin:cd.dolphin.ClientDolphin;


        constructor(clientDolphin:cd.dolphin.ClientDolphin){
            this.clientDolphin = clientDolphin;
            this.presentationModels = new map.dolphin.Map<string,pm.dolphin.ClientPresentationModel>();
            this.presentationModelsPerType = new map.dolphin.Map<string,pm.dolphin.ClientPresentationModel[]>();
            this.attributesPerId = new map.dolphin.Map<number,ca.dolphin.ClientAttribute>();
            this.attributesPerQualifier = new map.dolphin.Map<string,ca.dolphin.ClientAttribute[]>();
        }

        getClientDolphin(){
            return this.clientDolphin;
        }

        registerModel(model:pm.dolphin.ClientPresentationModel){
            var connector:cc.dolphin.ClientConnector = this.clientDolphin.getClientConnector();
            var createPMCommand:createPMCmd.dolphin.CreatePresentationModelCommand = new createPMCmd.dolphin.CreatePresentationModelCommand(model);
            console.log("about to send create presentation model command", createPMCommand);
            connector.send(createPMCommand,null);
            model.attributes.forEach( (attribute :ca.dolphin.ClientAttribute) => {
                this.addAttributeById(attribute);
                attribute.onValueChange((evt: ca.dolphin.ValueChangedEvent)=>{
                    var valueChangeCommand:valueChangedCmd.dolphin.ValueChangedCommand= new valueChangedCmd.dolphin.ValueChangedCommand(attribute.id.toString(),evt.oldValue,evt.newValue);
                    connector.send(valueChangeCommand,null);

                    if(attribute.qualifier){
                        this.addAttributeByQualifier(attribute);
                        var attrs = this.findAttributesByFilter((attr:ca.dolphin.ClientAttribute) =>{
                            return attr !== attribute && attr.qualifier === attribute.qualifier;
                        })
                        attrs.forEach((attr:ca.dolphin.ClientAttribute) =>{
                            attr.setValue(attribute.value);
                        })
                    }
                });

                attribute.onQualifierChange((evt:ca.dolphin.ValueChangedEvent)=> {
                    var changeAttrMetadataCmd:changeAttMD.dolphin.ChangeAttributeMetadataCommand =
                        new changeAttMD.dolphin.ChangeAttributeMetadataCommand(attribute.id.toString(), attr.dolphin.Attribute.QUALIFIER_PROPERTY, evt.newValue);
                    connector.send(changeAttrMetadataCmd, null);
                })
            });
        }

        add(model:pm.dolphin.ClientPresentationModel):boolean {
            if (!model) {
                return false;
            }
            if (this.presentationModels.containsKey(model.id)) {
                alert("There already is a PM with id " + model.id);
            }
            var added:boolean = false;
            if (!this.presentationModels.containsKey(model.id)) {
                this.presentationModels.put(model.id, model);
                this.addPresentationModelByType(model);
                this.registerModel(model);
                added = true;
            }

            console.log("client presentation model added and registered");
            return added;
        }

        remove(model:pm.dolphin.ClientPresentationModel):boolean {
            if (!model) {
                return false;
            }
            var removed:boolean = false;
            if (this.presentationModels.containsKey(model.id)) {
                this.removePresentationModelByType(model);
                this.presentationModels.remove(model.id);
                model.attributes.forEach((attribute:ca.dolphin.ClientAttribute) => {
                    //todo property change listener
                    this.removeAttributeById(attribute);
                    this.removeAttributeByQualifier(attribute);
                })


                removed = true;
            }
            return removed;
        }

        findAttributesByFilter(filter: (atr:ca.dolphin.ClientAttribute) => boolean){
            var matches:ca.dolphin.ClientAttribute[] = [];
            this.presentationModels.forEach((key:string, model:pm.dolphin.ClientPresentationModel) => {
                model.attributes.forEach((attr) =>{
                    if(filter(attr)){
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
            return this.presentationModels.keySet();
        }

        listPresentationModel():pm.dolphin.ClientPresentationModel[] {
            return this.presentationModels.values();
        }

        findPresentationModelById(id:string) {
            return this.presentationModels.get(id);
        }

        findAllPresentationModelByType(type:string):pm.dolphin.ClientPresentationModel[] {
            if (!type || !this.presentationModelsPerType.containsKey(type)) {
                return [];
            }
            return this.presentationModelsPerType.get(type);
        }

        deleteAllPresentationModelOfType(presentationModelType:string) {
            var presentationModels:pm.dolphin.ClientPresentationModel[] = this.findAllPresentationModelByType(presentationModelType);
            presentationModels.forEach(pm => {
                this.delete(pm, false);
            })
        }

        delete(model:pm.dolphin.ClientPresentationModel, notify:boolean) {
            if (!model) {
                return;
            }
            if (this.containsPresentationModel(model.id)) {
                this.remove(model);
                if (!notify || model.isClientSideOnly) {
                    return;
                }
                var connector:cc.dolphin.ClientConnector = this.clientDolphin.getClientConnector();
                connector.send(new dpmoftn.dolphin.DeleteAllPresentationModelsOfTypeNotification(model.presentationModelType), undefined);

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
            if (!attribute || !attribute.qualifier) {
                return;
            }
            var attributes:ca.dolphin.ClientAttribute[] = this.attributesPerQualifier.get(attribute.qualifier);
            if (!attributes) {
                attributes = [];
                this.attributesPerQualifier.put(attribute.qualifier, attributes);
            }
            if (!(attributes.indexOf(attribute) > -1)) {
                attributes.push(attribute);
            }

        }

        removeAttributeByQualifier(attribute:ca.dolphin.ClientAttribute) {
            if (!attribute || !attribute.qualifier) {
                return;
            }
            var attributes:ca.dolphin.ClientAttribute[] = this.attributesPerQualifier.get(attribute.qualifier);
            if (!attributes) {
                return;
            }
            if (attributes.length > -1) {
                attributes.splice(attributes.indexOf(attribute), 1);
            }
            if (attributes.length === 0) {
                this.attributesPerQualifier.remove(attribute.qualifier);
            }
        }
    }
}