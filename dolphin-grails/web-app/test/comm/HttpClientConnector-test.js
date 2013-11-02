define([
    'comm/NamedCommand',
    'comm/HttpClientConnector'
], function(NamedCommand, HttpClientConnector) {

    var assert = buster.assert;

    buster.testCase("HttpClientConnector", {

        "sends commands and calls onFinished correctly": function(done) {
            var me = this;

            this.connector = new HttpClientConnector(null, '/dummy');

            this.server = sinon.fakeServer.create();
            this.server.autoRespond = true;

            this.server.respondWith(
                    "POST",
                    "/dummy",
                    [
                        200,
                        { "Content-Type": "application/json" },
                        '[]'
                    ]);

            var command = new NamedCommand("aCommand");
            this.connector.send(command, function(response) {
                console.log("got test response", response);
                assert.equals([], response);

                var request = me.server.requests[0];
                assert.equals(200, request.status);
                assert.equals("POST", request.method);
                assert.equals(
                        '[{"id":"aCommand","className":"org.opendolphin.core.comm.NamedCommand"}]',
                        request.requestBody);

                done();
            });
        },

        "sends multiple commands synchronously in order": function(done) {
            var me = this;

            buster.testRunner.timeout = 2000;

            this.connector = new HttpClientConnector(null, '/dummy');

            this.connector._executeSend = function(cmd) {
                var ajaxDfd = $.Deferred();
                var _resolve = function(text) {
                    console.log("resolving "+text);
                    cmd.onFinished(text+" ok");
                    cmd.sendDfd.resolve();
                    ajaxDfd.resolve();
                };

                if (cmd.data.indexOf("testA") != -1) {
                    // defer resolution of first command
                    setTimeout(function() {
                        _resolve("testA");
                    }, 1000);
                } else {
                    _resolve("testB");
                }
                return ajaxDfd.promise();
            };

            var receivedResponses = [];

            var sendCommand = function(cmd) {
                return me.connector.send(cmd, function(response) {
                    console.log("onFinished", response);
                    receivedResponses.push(cmd);
                });
            };

            var commandA = new NamedCommand("testA");
            var commandB = new NamedCommand("testB");

            sendCommand(commandA)
                .done(function() {
                    console.log("sent commandA");
                    assert.equals(1, receivedResponses.length);
                    assert.equals(commandA, receivedResponses[0]);
                });

            sendCommand(commandB)
                .done(function() {
                    console.log("sent commandB");
                    assert.equals(2, receivedResponses.length);
                    assert.equals(commandB, receivedResponses[1]);
                    done();
                });

        }

    });

});

