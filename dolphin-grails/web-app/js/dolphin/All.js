define(["require", "exports", '../../js/dolphin/ClientAttribute', '../../js/dolphin/ClientDolphin', '../../js/dolphin/ClientModelStore', '../../js/dolphin/ClientConnector', '../../js/dolphin/NoTransmitter', '../../js/dolphin/HttpTransmitter'], function(require, exports, __cat__, __dol__, __mst__, __cc__, __ntm__, __htm__) {
    var cat = __cat__;
    var dol = __dol__;
    var mst = __mst__;
    var cc = __cc__;
    var ntm = __ntm__;
    var htm = __htm__;

    /**
    * JS-friendly facade to avoid too many dependencies in plain JS code
    */
    // factory method for the initialized dolphin
    function dolphin(url, reset) {
        var dolphin = new dol.dolphin.ClientDolphin();
        var transmitter;
        if (url != null && url.length > 0) {
            // todo dk: delete the session cookie if reset is true
            transmitter = new htm.dolphin.HttpTransmitter(url);
        } else {
            transmitter = new ntm.dolphin.NoTransmitter();
        }
        dolphin.setClientConnector(new cc.dolphin.ClientConnector(transmitter, dolphin));
        dolphin.setClientModelStore(new mst.dolphin.ClientModelStore(dolphin));
        return dolphin;
    }
    exports.dolphin = dolphin;

    // factory method for attributes
    function attribute(propertyName, qualifier, value) {
        return new cat.dolphin.ClientAttribute(propertyName, qualifier, value);
    }
    exports.attribute = attribute;
});
//# sourceMappingURL=All.js.map
