import cat = require('../../js/dolphin/ClientAttribute');
import dol = require('../../js/dolphin/ClientDolphin');
import mst = require('../../js/dolphin/ClientModelStore');
import cc  = require('../../js/dolphin/ClientConnector');
import ntm = require('../../js/dolphin/NoTransmitter');
import htm = require('../../js/dolphin/HttpTransmitter');

/**
 * JS-friendly facade to avoid too many dependencies in plain JS code.
 * The name of this file is also used for the initial lookup of the
 * one javascript file that contains all the dolphin code.
 * Changing the name requires the build support and all users
 * to be updated as well.
 * Dierk Koenig
 */

// factory method for the initialized dolphin
export function dolphin(url : string, reset : boolean, slackMS: number = 300) : dol.dolphin.ClientDolphin  {
    var dolphin = new dol.dolphin.ClientDolphin();
    var transmitter ;
    if (url != null && url.length > 0) {
        transmitter = new htm.dolphin.HttpTransmitter(url, reset);
    } else {
        transmitter = new ntm.dolphin.NoTransmitter();
    }
    dolphin.setClientConnector(new cc.dolphin.ClientConnector(transmitter, dolphin, slackMS));
    dolphin.setClientModelStore(new mst.dolphin.ClientModelStore(dolphin));
    return dolphin;
}