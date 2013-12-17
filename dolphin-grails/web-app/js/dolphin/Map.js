define(["require", "exports"], function(require, exports) {
    (function (dolphin) {
        var Map = (function () {
            function Map() {
                this.keys = [];
                this.data = [];
            }
            Map.prototype.put = function (key, value) {
                if (!this.containsKey(key)) {
                    this.keys.push(key);
                }
                this.data[this.keys.indexOf(key)] = value;
            };

            Map.prototype.get = function (key) {
                return this.data[this.keys.indexOf(key)];
            };

            Map.prototype.remove = function (key) {
                if (this.containsKey(key)) {
                    var index = this.keys.indexOf(key);
                    this.keys.splice(index, 1);
                    this.data.splice(index, 1);
                    return true;
                }
                return false;
            };

            Map.prototype.isEmpty = function () {
                return this.keys.length == 0;
            };

            Map.prototype.length = function () {
                return this.keys.length;
            };

            Map.prototype.forEach = function (handler) {
                for (var i = 0; i < this.keys.length; i++) {
                    handler(this.keys[i], this.data[i]);
                }
            };

            Map.prototype.containsKey = function (key) {
                return this.keys.indexOf(key) > -1;
            };

            Map.prototype.containsValue = function (value) {
                return this.data.indexOf(value) > -1;
            };

            Map.prototype.values = function () {
                return this.data.slice(0);
            };

            Map.prototype.keySet = function () {
                return this.keys.slice(0);
            };
            return Map;
        })();
        dolphin.Map = Map;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=Map.js.map
