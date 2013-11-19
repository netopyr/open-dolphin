import tsUnit = require('./tsUnit');
import cat    = require('../test/dolphin/ClientAttributeTests');

// new instance of tsUnit
var test = new tsUnit.tsUnit.Test();

// add your test class (you can call this multiple times)
test.addTestClass(new cat.dolphin.ClientAttributeTests());

// Use the built in results display
test.showResults(document.getElementById('results'), test.run());