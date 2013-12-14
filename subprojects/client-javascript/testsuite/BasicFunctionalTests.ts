import cat = require('../js/dolphin/ClientAttribute');
import dol = require('../js/dolphin/ClientDolphin');
import mst = require('../js/dolphin/ClientModelStore');
import cc  = require('../js/dolphin/ClientConnector');
import ntm = require('../js/dolphin/NoTransmitter');

// setting up the dolphin

var dolphin   = new dol.dolphin.ClientDolphin();
var connector = new cc.dolphin.ClientConnector(new ntm.dolphin.NoTransmitter(),dolphin);
dolphin.setClientModelStore(new mst.dolphin.ClientModelStore(dolphin));

// make a presentation model

var instantUpdateAttribute = new cat.dolphin.ClientAttribute("instant-update",  "same-qualifier","");
var qualifiedAttribute     = new cat.dolphin.ClientAttribute("qualified-update","same-qualifier","");

dolphin.presentationModel(undefined, undefined, instantUpdateAttribute );
dolphin.presentationModel(undefined, undefined, qualifiedAttribute );

// binding

var instantUpdateInput = <HTMLInputElement> document.getElementById("instant-update-input");
var instantUpdateLabel = <HTMLDivElement>   document.getElementById("instant-update-label");
var qualifiedLabel     = <HTMLDivElement>   document.getElementById("qualified-label");

instantUpdateInput.oninput = (event: Event) => {
    instantUpdateAttribute.setValue(instantUpdateInput.value);
};
instantUpdateAttribute.onValueChange( (event: cat.dolphin.ValueChangedEvent) => {
    instantUpdateLabel.innerHTML = event.newValue;
});
qualifiedAttribute.onValueChange( (event: cat.dolphin.ValueChangedEvent) => {
    qualifiedLabel.innerHTML = event.newValue;
});
