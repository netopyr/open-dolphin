define([
    'comm/Command'
], function(Command) {

    var assert = buster.assert;

    buster.testCase("Command", {

        "has an id": function () {
            var command = new Command();
            assert.equals("dolphin-core-command", command.getId());
        }

    });

});

