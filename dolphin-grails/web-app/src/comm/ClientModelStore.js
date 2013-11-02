define([
    'comm/CreatePresentationModelCommand',
    'comm/ValueChangedCommand'
], function(CreatePresentationModelCommand, ValueChangedCommand) {

    return function(clientDolphin) {

        this.clientDolphin = clientDolphin;

        this.models = [];

        this.getClientDolphin = function() {
            return this.clientDolphin;
        };

        this.registerModel = function(model) {
            var me = this;

            var connector = this.clientDolphin.getClientConnector();

            var createCmd = new CreatePresentationModelCommand(model);
            console.log("about to send create pm", createCmd);
            connector.send(createCmd);

            model.attributes.forEach(function(attr) {
                attr.on("valueChange", function(data) {
                    var cmd = new ValueChangedCommand(attr.id, data.oldValue, data.newValue);
                    console.log("about to send value changed", cmd);
                    connector.send(cmd);

                    if (attr.qualifier) {
                        var attrs = me.findAttributesByFilter(function(a) {
                            return a !== attr && a.qualifier === attr.qualifier;
                        });
                        attrs.forEach(function(a) {
                            a.setValue(attr.getValue());
                        })
                    }
                });
                attr.stored = true;
            })
        };

        this.add = function(model) {
            // TODO check if model exists already
            this.models.push(model);

            // TODO bind client connector to PM attributes
            this.registerModel(model);

            console.log("model added and registerd");
        };

        /**
         * @param filter the filter function
         * @returns an array of matches or an empty array otherwise
         */
        this.findAttributesByFilter = function(filter) {
            if (typeof filter != 'function') {
                throw new Error("Argument filter must be a function");
            }
            var matches = [];
            this.models.forEach(function(model) {
                model.attributes.forEach(function(attr) {
                    if (filter(attr)) {
                        matches.push(attr);
                    }
                });
            });
            return matches;
        };

    };

});

