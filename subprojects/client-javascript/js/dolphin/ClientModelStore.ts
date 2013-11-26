import pm               = require("../../js/dolphin/ClientPresentationModel")
import cd               = require("../../js/dolphin/ClientDolphin")
import cc               = require("../../js/dolphin/ClientConnector")
import createPMCmd      = require("../../js/dolphin/CreatePresentationModelCommand")
import ca               = require("../../js/dolphin/ClientAttribute");
import valueChangedCmd  = require("../../js/dolphin/ValueChangedCommand")
import changeAttMD      = require("../../js/dolphin/ChangeAttributeMetadataCommand")
import attr             = require("../../js/dolphin/Attribute")
import map              = require("../../js/dolphin/Map")

export module dolphin {

    export class ClientModelStore {

        private presentationModels:map.dolphin.Map<string,pm.dolphin.ClientPresentationModel>;
        private presentationModelsPerType:map.dolphin.Map<string,pm.dolphin.ClientPresentationModel[]>;


        private clientDolphin:cd.dolphin.ClientDolphin;


        constructor(clientDolphin:cd.dolphin.ClientDolphin){
            this.clientDolphin = clientDolphin;
            this.presentationModels = new map.dolphin.Map<string,pm.dolphin.ClientPresentationModel>();
            this.presentationModelsPerType = new map.dolphin.Map<string,pm.dolphin.ClientPresentationModel[]>();
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
                attribute.onValueChange((evt: ca.dolphin.ValueChangedEvent)=>{
                    var valueChangeCommand:valueChangedCmd.dolphin.ValueChangedCommand= new valueChangedCmd.dolphin.ValueChangedCommand(attribute.id.toString(),evt.oldValue,evt.newValue);
                    connector.send(valueChangeCommand,null);

                    if(attribute.qualifier){
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
            if (this.presentationModels.containsKey(model.id)) {
                alert("There already is a PM with id " + model.id);
            }
            var added:boolean = false;
            if (!this.presentationModels.containsKey(model.id)) {
                this.presentationModels.put(model.id, model);
                this.registerModel(model);
                added = true;
            }

            console.log("client presentation model added and registered");
            return added;
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
    }
}