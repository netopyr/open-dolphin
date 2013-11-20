import tsUnit = require('./tsUnit');
import cat    = require('../test/dolphin/ClientAttributeTests');
import cpmt   = require('../test/dolphin/ClientPresentationModelTests');
import namedCmdt   = require('../test/dolphin/NamedCommandTests');
import valChangedCmdt   = require('../test/dolphin/ValueChangedCommandTests');
import changedAttrMDCmdt   = require('../test/dolphin/ChangeAttributeMetadataCommandTests');
import emptyNt   = require('../test/dolphin/EmptyNotificationTests');
import createPMCmdt   = require('../test/dolphin/CreatePresentationModelCommandTests');
import cdt   = require('../test/dolphin/ClientDolphinTests');


// new instance of tsUnit
var test = new tsUnit.tsUnit.Test();

// add your test class (you can call this multiple times)
test.addTestClass(new cat.dolphin.ClientAttributeTests());
test.addTestClass(new cpmt.dolphin.ClientPresentationModelTests());
test.addTestClass(new namedCmdt.dolphin.NamedCommandTests());
test.addTestClass(new valChangedCmdt.dolphin.ValueChangedCommandTests());
test.addTestClass(new valChangedCmdt.dolphin.ValueChangedCommandTests());
test.addTestClass(new changedAttrMDCmdt.dolphin.ChangeAttributeMetadataCommandTests());
test.addTestClass(new emptyNt.dolphin.EmptyNotificationTests());
test.addTestClass(new createPMCmdt.dolphin.CreatePresentationModelCommandTests());
test.addTestClass(new cdt.dolphin.ClientDolphinTests());

// Use the built in results display
test.showResults(document.getElementById('results'), test.run());