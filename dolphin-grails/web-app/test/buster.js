var config = exports; // Vanity

config["Browser tests"] = {
    environment: "browser",
    rootPath: "../",
    libs: [
      "libs/require.js",
      "libs/jquery.js",
      "config.js"
    ],
    sources: [
        "src/**/*.js"
    ],
    tests: [
        "test/**/*-test.js"
    ],
    extensions: [require('buster-amd')],
    "buster-amd": {
        pathMapper: function (path) {
          return path.
                 // remove extension
                 replace(/\.js$/, "").
                 // replace leading slash with previous directory for test files
                 replace(/^\//, "../");
        }
    }
};
