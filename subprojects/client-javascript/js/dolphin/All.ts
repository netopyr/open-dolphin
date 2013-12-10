import cat = require('../../js/dolphin/ClientAttribute');
import dol = require('../../js/dolphin/ClientDolphin');
import mst = require('../../js/dolphin/ClientModelStore');
import cc  = require('../../js/dolphin/ClientConnector');
import ntm = require('../../js/dolphin/NoTransmitter');
import htm = require('../../js/dolphin/HttpTransmitter');

/**
 * JS-friendly facade to avoid too many dependencies in plain JS code
 */

// factory method for the initialized dolphin
export function dolphin(url : string, reset : boolean) : dol.dolphin.ClientDolphin  {
    var dolphin = new dol.dolphin.ClientDolphin();
    var transmitter ;
    if (url != null && url.length > 0) {
        // todo dk: delete the session cookie if reset is true
        transmitter = new htm.dolphin.HttpTransmitter(url);
    } else {
        transmitter = new ntm.dolphin.NoTransmitter();
    }
    dolphin.setClientConnector(new cc.dolphin.ClientConnector(transmitter));
    dolphin.setClientModelStore(new mst.dolphin.ClientModelStore(dolphin));
    return dolphin;
}

// factory method for attributes
export function attribute(propertyName, qualifier, value) {
    return new cat.dolphin.ClientAttribute(propertyName, qualifier, value);
}





