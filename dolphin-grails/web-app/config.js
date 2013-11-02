require.config({

    baseUrl: 'src/',

    paths: {
        jquery : '../libs/jquery'
    },

    shim: {
        'jquery': {
            exports: '$'
        }
    },

    map : {
        '*': {
            $ : 'jquery'
        }
    }

});