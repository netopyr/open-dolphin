
var dol = require('ClientDolphin');
var mst = require('ClientModelStore');
var cc = require('ClientConnector');
var ntm = require('NoTransmitter');
var htm = require('HttpTransmitter');

/**
* JS-friendly facade to avoid too many dependencies in plain JS code.
* The name of this file is also used for the initial lookup of the
* one javascript file that contains all the dolphin code.
* Changing the name requires the build support and all users
* to be updated as well.
* Dierk Koenig
*/
// factory method for the initialized dolphin
function dolphin(url, reset, slackMS) {
    if (typeof slackMS === "undefined") { slackMS = 300; }
    var dolphin = new dol.dolphin.ClientDolphin();
    var transmitter;
    if (url != null && url.length > 0) {
        transmitter = new htm.dolphin.HttpTransmitter(url, reset);
    } else {
        transmitter = new ntm.dolphin.NoTransmitter();
    }
    dolphin.setClientConnector(new cc.dolphin.ClientConnector(transmitter, dolphin, slackMS));
    dolphin.setClientModelStore(new mst.dolphin.ClientModelStore(dolphin));
    return dolphin;
}
exports.dolphin = dolphin;

define("OpenDolphin", function(){});
