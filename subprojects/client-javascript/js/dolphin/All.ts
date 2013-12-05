import cat = require('../../js/dolphin/ClientAttribute');
import dol = require('../../js/dolphin/ClientDolphin');
import mst = require('../../js/dolphin/ClientModelStore');
import cc  = require('../../js/dolphin/ClientConnector');
import ntm = require('../../js/dolphin/NoTransmitter');

/**
 * JS-friendly facade to avoid too many dependencies in plain JS code
 */

// setting up the dolphin
export var dolphin = new dol.dolphin.ClientDolphin();
var connector = new cc.dolphin.ClientConnector(new ntm.dolphin.NoTransmitter());
dolphin.setClientConnector(connector);
dolphin.setClientModelStore(new mst.dolphin.ClientModelStore(dolphin));

// factory method for attributes
export function attribute(propertyName, qualifier) {
    return new cat.dolphin.ClientAttribute(propertyName, qualifier);
}





