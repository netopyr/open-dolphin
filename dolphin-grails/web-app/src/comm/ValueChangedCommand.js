define([
    'comm/Command'
], function (Command) {

    var ValueChangedCommand = function(attributeId, oldValue, newValue) {

        this.id = 'ValueChanged';
        this.className = "org.opendolphin.core.comm.ValueChangedCommand";

        this.attributeId = attributeId;
        this.oldValue = oldValue;
        this.newValue = newValue;

    };

    ValueChangedCommand.prototype = new Command();

    return ValueChangedCommand

});
