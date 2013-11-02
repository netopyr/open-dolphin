define(function () {

    function PresentationModel(id, type) {

        this.id = id || PresentationModel.nextId();
        this.presentationModelType = type;

        this.attributes = [];

        this.addAttribute = function(attribute) {
            this.attributes.push(attribute);
        }

    }

    PresentationModel.nextId = (function() {
        var id = 0;
        return function() {
            return id++;
        }
    })();

    return PresentationModel;

});
