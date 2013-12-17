define(["require", "exports"], function(require, exports) {
    
    
    (function (dolphin) {
        /**
        * A transmitter that is not transmitting at all.
        * It may serve as a stand-in when no real transmitter is needed.
        */
        var NoTransmitter = (function () {
            function NoTransmitter() {
            }
            NoTransmitter.prototype.transmit = function (commands, onDone) {
                // do nothing special
                onDone([]);
            };
            return NoTransmitter;
        })();
        dolphin.NoTransmitter = NoTransmitter;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=NoTransmitter.js.map
