define([
    'comm/Command'
], function (Command) {

    var NamedCommand = function(name) {

        this.id = name;

        this.className = "org.opendolphin.core.comm.NamedCommand";

    };

    NamedCommand.prototype = new Command();

    return NamedCommand

});
