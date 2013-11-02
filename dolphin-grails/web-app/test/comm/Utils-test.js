define([
    'comm/Utils'
], function(Utils) {

    var assert = buster.assert;

    buster.testCase("Utils", {

        "implements stable hashCode for undefined": function () {
            assert.equals(0, Utils.hashCode(undefined));
        },

        "implements stable hashCode for null": function () {
            assert.equals(3392903, Utils.hashCode(null));
        },

        "implements stable hashCode for empty string": function () {
            assert.equals(1088, Utils.hashCode(""));
        },

        "implements stable hashCode for non-empty string": function () {
            assert.equals(1083642606, Utils.hashCode("test"));
        },

        "implements stable hashCode for number": function () {
            assert.equals(49, Utils.hashCode(1));
        },

        "implements stable hashCode for boolean": function () {
            assert.equals(3569038, Utils.hashCode(true));
        },

        "implements stable hashCode for empty String object": function () {
            assert.equals(1088, Utils.hashCode(new String("")));
        },

        "implements stable hashCode for non-empty String object": function () {
            assert.equals(1083642606, Utils.hashCode(new String("test")));
        },

        "implements stable hashCode for date": function () {
            assert.equals(-638476638, Utils.hashCode(new Date("01/01/01")));
        },

        "implements stable hashCode for empty object": function () {
            assert.equals(3938, Utils.hashCode({}));
        },

        "implements stable hashCode for function": function () {
            assert.equals(0, Utils.hashCode(function() {}));
        },

        "returns different hashCodes for different strings": function () {
            refute.equals(Utils.hashCode("test1"), Utils.hashCode("test2"));
        },

       "returns different hashCodes for different numbers": function () {
            refute.equals(Utils.hashCode(1), Utils.hashCode(2));
        },

       "returns different hashCodes for different booleans": function () {
            refute.equals(Utils.hashCode(true), Utils.hashCode(false));
        },

       "returns different hashCodes for different String objects": function () {
            refute.equals(Utils.hashCode(new String("test1")), Utils.hashCode(new String("test2")));
        },

       "returns different hashCodes for different Date objects": function () {
            refute.equals(Utils.hashCode(new Date("02/02/02")), Utils.hashCode(new Date("01/01/01")));
        },

       "returns different hashCodes for different objects": function () {
            refute.equals(Utils.hashCode({id: "test1"}), Utils.hashCode({id: "test2"}));
        },

       "returns same hashCode for different functions": function () {
            assert.equals(Utils.hashCode(function() {return 1}), Utils.hashCode(function() {return 2}));
        }

    });

});

