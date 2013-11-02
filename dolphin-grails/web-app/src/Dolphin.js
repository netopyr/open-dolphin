define([
    'comm/ClientDolphin',
    'comm/ClientModelStore',
    'comm/HttpClientConnector'
], function (ClientDolphin, ClientModelStore, HttpClientConnector) {

    return function(serverUrl) {

        this.clientDolphin = new ClientDolphin();
        this.clientDolphin.setClientModelStore(new ClientModelStore(this.clientDolphin));

        var connector = new HttpClientConnector(this.clientDolphin, serverUrl);
        this.clientDolphin.setClientConnector(connector);

        this.getClientDolphin = function() {
            return this.clientDolphin;
        };

        console.log("dolphin started", serverUrl);

    };

});
