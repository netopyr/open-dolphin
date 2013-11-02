define(function () {

    function EventBus() {

        // better solutions for inheritance are welcome
        this._assertSubscribers = function() {
            if (!this.subscribers) {
                this.subscribers = {};
            }
        };

        this.on = function(type, subscriber) {
            if (!type || typeof type != 'string') {
                throw new Error("Argument type must be a string");
            }
            if (typeof subscriber != 'function') {
                throw new Error("Argument subscriber must be a function");
            }
            this._assertSubscribers();
            var subs = this.subscribers[type];
            if (!subs) {
                this.subscribers[type] = [];
                subs = this.subscribers[type];
            }
            if (subs.indexOf(subscriber) === -1) {
                subs.push(subscriber);
            }
        };

        this.trigger = function(type, data) {
            this._assertSubscribers();
            var subs = this.subscribers[type];
            if (subs) {
                subs.forEach(function(sub) {
                    sub(data);
                })
            }
        }

    }

    return EventBus;

});
