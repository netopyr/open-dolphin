define(function () {

    return function() {

        this.encode = function(commands) {
            return JSON.stringify(commands);
        };

        this.decode = function(transmitted) {
            if (typeof transmitted == 'string') {
                return JSON.parse(transmitted);
            } else {
                return transmitted;
            }
        };

    };

});
