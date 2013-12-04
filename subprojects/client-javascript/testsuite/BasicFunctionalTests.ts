import cat = require('../js/dolphin/ClientAttribute');
import dol = require('../js/dolphin/ClientDolphin');
import mst = require('../js/dolphin/ClientModelStore');
import cc  = require('../js/dolphin/ClientConnector');
import htm = require('../js/dolphin/HttpTransmitter');

// setting up the dolphin

// to later set into the transmitter
// var dolphinUrl = "http://localhost:8080/dolphin-grails/dolphin/";
var dolphin   = new dol.dolphin.ClientDolphin();
var connector = new cc.dolphin.ClientConnector(new htm.dolphin.HttpTransmitter());
dolphin.setClientConnector(connector);
dolphin.setClientModelStore(new mst.dolphin.ClientModelStore(dolphin));

// make a presentation model

var instantUpdateAttribute = new cat.dolphin.ClientAttribute("instant-update",  "same-qualifier");
var qualifiedAttribute     = new cat.dolphin.ClientAttribute("qualified-update","same-qualifier");

console.log("INIT PM");
dolphin.presentationModel(undefined, undefined, instantUpdateAttribute );
dolphin.presentationModel(undefined, undefined, qualifiedAttribute );

// binding

var instantUpdateInput = <HTMLInputElement> document.getElementById("instant-update-input");
var instantUpdateLabel = <HTMLDivElement>   document.getElementById("instant-update-label");
var qualifiedLabel     = <HTMLDivElement>   document.getElementById("qualified-label");

instantUpdateInput.oninput =  (event: Event) => {
    instantUpdateAttribute.setValue(instantUpdateInput.value);
};
instantUpdateAttribute.onValueChange( (event: cat.dolphin.ValueChangedEvent) => {
    instantUpdateLabel.innerHTML = event.newValue;
});
qualifiedAttribute.onValueChange( (event: cat.dolphin.ValueChangedEvent) => {
    qualifiedLabel.innerHTML = event.newValue;
});
