define([
    'Dolphin'
], function(Dolphin) {

    var assert = buster.assert;

    buster.testCase("Dolphin", {

        "is initialized with connector and model store": function () {

            var serverUrl = "http://localhost:8888/myFirstDolphin/tutorial/";
            var dolphin = new Dolphin(serverUrl);
            var clientDolphin = dolphin.getClientDolphin();

            var clientConnector = clientDolphin.getClientConnector();
            assert(clientConnector);

            assert(clientDolphin === clientConnector.getClientDolphin());
            assert.equals(serverUrl, clientConnector.getServerUrl());


            var clientModelStore = clientDolphin.getClientModelStore();
            assert(clientModelStore);
            assert(clientDolphin === clientModelStore.getClientDolphin());
        }

    });

});

