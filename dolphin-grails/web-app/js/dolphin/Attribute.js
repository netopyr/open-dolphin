define(["require", "exports"], function(require, exports) {
    (function (dolphin) {
        var Attribute = (function () {
            function Attribute() {
            }
            Attribute.QUALIFIER_PROPERTY = "qualifier";
            Attribute.DIRTY_PROPERTY = "dirty";
            Attribute.BASE_VALUE = "baseValue";
            Attribute.VALUE = "value";
            Attribute.TAG = "tag";
            return Attribute;
        })();
        dolphin.Attribute = Attribute;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=Attribute.js.map
