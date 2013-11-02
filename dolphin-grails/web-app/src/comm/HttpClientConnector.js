define([
    '$',
    'comm/Codec',
    'comm/ClientAttribute'
], function ($, Codec, ClientAttribute) {

    return function(clientDolphin, serverUrl) {
        this.codec = new Codec();

        this.clientDolphin = clientDolphin;
        this.serverUrl = serverUrl;

        this.getClientDolphin = function() {
            return this.clientDolphin;
        };

        this.getServerUrl = function() {
            return this.serverUrl;
        };

        var sending = false;
        var pendingCommands = [];

        this._dispatchCommand = function(command) {
            console.log("dispatching command", command);
            var modelStore = this.clientDolphin.getClientModelStore();

            switch(command.id) {
                case 'CreatePresentationModel':
                    var clientAttributes = command.attributes.map(function(attr) {
                        var clientAttr = new ClientAttribute(attr.propertyName);
                        clientAttr.value = attr.value;
                        clientAttr.baseValue = attr.baseValue;
                        clientAttr.qualifier = attr.qualifier;
                        return clientAttr;
                    });
                    return this.clientDolphin.presentationModel(command.pmId, command.pmType, clientAttributes);
                case 'InitializeAttribute':
                    break;
                case 'ValueChanged':
                    var idMatch = function (attr) {
                        return (attr.id == command.attributeId)
                    };
                    var attrs = modelStore.findAttributesByFilter(idMatch);
                    if (attrs.length > 1) {
                        throw new Error("Found multiple attributes with id "+command.attributeId);
                    }
                    if (attrs.length == 1) {
                        attrs[0].setValue(command.newValue);
                    }
                    break;
            }
            return undefined;
        };

        this._executeSend = function(pendingCmd) {
            var me = this;

            var data = pendingCmd.data;
            var onFinished = pendingCmd.onFinished;
            var sendDfd = pendingCmd.sendDfd;
            return $.ajax({
                    type: 'POST',
                    url: this.serverUrl,
                    data: data
                })
                .done(function (response) {
                    console.log("received server response", response);
                    var commands = me.codec.decode(response);
                    var models = [];
                    commands.forEach(function(cmd) {
                        var model = me._dispatchCommand(cmd);
                        console.log("dispatch result", cmd, model);
                        if (model) {
                            models.push(model);
                        }
                    });
                    if (onFinished) {
                        onFinished(models);
                    }
                    sendDfd.resolve(models);
                })
                .fail(function (error) {
                    console.log("send error", pendingCmd, error);
                    sendDfd.reject(error);
                });
        };

        /**
         * @param command the command to send
         * @param onFinished the success callback
         * @returns send result promise (success, error)
         */
        this.send = function(command, onFinished) {
            var me = this;

            var dfd = $.Deferred();

            pendingCommands.push({
                data: this.codec.encode([command]),
                onFinished: onFinished,
                sendDfd: dfd
            });

            console.log("command queued", command);

            function _sendNext() {
                var next = pendingCommands.shift();
                if (next) {
                    console.log("sending ...", next);
                    sending = me._executeSend(next)
                        .always(function() {
                            _sendNext();
                        });
                } else {
                    console.log("nothing left to send");
                    sending = undefined;
                }
            }

            if (!sending) {
                _sendNext();
            }

            return dfd.promise();
        }

    };

});
