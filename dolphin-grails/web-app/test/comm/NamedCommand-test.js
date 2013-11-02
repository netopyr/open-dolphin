define([
    'comm/NamedCommand'
], function(NamedCommand) {

    var assert = buster.assert;

    buster.testCase("NamedCommand", {

        "has a custom id": function () {
            var command = new NamedCommand("myId");
            assert.equals("myId", command.getId());
        }

    });

});

