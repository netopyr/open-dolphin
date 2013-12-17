define(["require", "exports", "../../js/dolphin/Codec"], function(require, exports, __cod__) {
    
    
    var cod = __cod__;

    (function (dolphin) {
        var HttpTransmitter = (function () {
            function HttpTransmitter(url) {
                this.url = url;
                this.http = new XMLHttpRequest();
                this.http.withCredentials = true;
                this.codec = new cod.dolphin.Codec();

                this.invalidate();
            }
            HttpTransmitter.prototype.transmit = function (commands, onDone) {
                var _this = this;
                this.http.onerror = function (evt) {
                    alert("could not fetch " + _this.url + ", message: " + evt.message);
                    onDone([]);
                };

                this.http.onloadend = function (evt) {
                    var responseText = _this.http.responseText;
                    console.log("got: " + responseText);
                    var responseCommands = _this.codec.decode(responseText);
                    onDone(responseCommands);
                };

                this.http.open('POST', this.url, true);
                this.http.send(this.codec.encode(commands));
            };

            HttpTransmitter.prototype.invalidate = function () {
                this.http.open('POST', this.url + 'invalidate', true);
                this.http.send();
            };
            return HttpTransmitter;
        })();
        dolphin.HttpTransmitter = HttpTransmitter;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=HttpTransmitter.js.map
