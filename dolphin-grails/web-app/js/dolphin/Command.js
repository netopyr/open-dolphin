define(["require", "exports"], function(require, exports) {
    (function (dolphin) {
        var Command = (function () {
            function Command() {
                this.id = "dolphin-core-command";
            }
            return Command;
        })();
        dolphin.Command = Command;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=Command.js.map
