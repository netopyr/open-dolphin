define(function () {

    var Utils = {};

    Utils.hashCode = function(object) {
        var hash = 0;
        var string = "";
        if (object !== undefined && typeof object !== "function") {
            string = JSON.stringify(object);
        }
        for (var i = 0; i < string.length; i++) {
            var varchar = string.charCodeAt(i);
            hash = ((hash<<5)-hash)+varchar;
            hash = hash & hash; // Convert to 32bit integer
        }
        return hash;
    };

    return Utils;

});
