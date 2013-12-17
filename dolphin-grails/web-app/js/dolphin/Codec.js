define(["require", "exports"], function(require, exports) {
    (function (dolphin) {
        var Codec = (function () {
            function Codec() {
            }
            Codec.prototype.encode = function (commands) {
                return JSON.stringify(commands);
            };

            Codec.prototype.decode = function (transmitted) {
                if (typeof transmitted == 'string') {
                    return JSON.parse(transmitted);
                } else {
                    return transmitted;
                }
            };
            return Codec;
        })();
        dolphin.Codec = Codec;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=Codec.js.map
