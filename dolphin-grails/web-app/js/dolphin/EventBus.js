define(["require", "exports"], function(require, exports) {
    (function (dolphin) {
        var EventBus = (function () {
            function EventBus() {
                this.eventHandlers = [];
            }
            EventBus.prototype.onEvent = function (eventHandler) {
                this.eventHandlers.push(eventHandler);
            };
            EventBus.prototype.trigger = function (event) {
                this.eventHandlers.forEach(function (handle) {
                    return handle(event);
                });
            };
            return EventBus;
        })();
        dolphin.EventBus = EventBus;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=EventBus.js.map
