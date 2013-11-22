import pm               = require("../../js/dolphin/ClientPresentationModel")
import cd               = require("../../js/dolphin/ClientDolphin")
import cc               = require("../../js/dolphin/ClientConnector")
import createPMCmd      = require("../../js/dolphin/CreatePresentationModelCommand")
import ca               = require("../../js/dolphin/ClientAttribute");
import valueChangedCmd  = require("../../js/dolphin/ValueChangedCommand")

export module dolphin {

    export class ClientModelStore {

        private clientDolphin:cd.dolphin.ClientDolphin;
        models : pm.dolphin.ClientPresentationModel[] = [];

        constructor(clientDolphin:cd.dolphin.ClientDolphin){
            this.clientDolphin = clientDolphin;
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
                })

                //TODO qualifier change
            });
        }
        add(model:pm.dolphin.ClientPresentationModel){
            this.models.push(model);
            this.registerModel(model);
            console.log("client presentation model added and registered");
        }

        findAttributesByFilter(filter: (atr:ca.dolphin.ClientAttribute) => boolean){
            var matches:ca.dolphin.ClientAttribute[] = [];
            this.models.forEach((model:pm.dolphin.ClientPresentationModel)=>{
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