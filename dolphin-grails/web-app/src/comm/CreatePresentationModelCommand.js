define([
    'comm/Command'
], function (Command) {

    var CreatePresentationModelCommand = function(model) {

        this.id = "CreatePresentationModel";
        this.className = "org.opendolphin.core.comm.CreatePresentationModelCommand";

        this.pmId = model.id;
        this.pmType = model.presentationModelType;
//        this.clientSideOnly = false;

        var attributes = [];

        model.attributes.forEach(function(attr) {
            attributes.push({
                propertyName:   attr.propertyName,
                id:             attr.id,
                qualifier:      attr.qualifier,
                value:          attr.value,
                tag:            attr.tag
            });
        });

        this.attributes = attributes;

    };

    CreatePresentationModelCommand.prototype = new Command();

    return CreatePresentationModelCommand;

});
