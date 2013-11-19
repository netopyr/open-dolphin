import tsUnit = require('./tsUnit');
import cat    = require('../test/dolphin/ClientAttributeTests');
import cpmt   = require('../test/dolphin/ClientPresentationModelTests');

// new instance of tsUnit
var test = new tsUnit.tsUnit.Test();

// add your test class (you can call this multiple times)
test.addTestClass(new cat.dolphin.ClientAttributeTests());
test.addTestClass(new cpmt.dolphin.ClientPresentationModelTests());

// Use the built in results display
test.showResults(document.getElementById('results'), test.run());