import tsUnit               = require('./tsUnit');
import cat                  = require('../test/dolphin/ClientAttributeTests');
import cpmt                 = require('../test/dolphin/ClientPresentationModelTests');
import namedCmdt            = require('../test/dolphin/NamedCommandTests');
import valChangedCmdt       = require('../test/dolphin/ValueChangedCommandTests');
import changedAttrMDCmdt    = require('../test/dolphin/ChangeAttributeMetadataCommandTests');
import emptyNt              = require('../test/dolphin/EmptyNotificationTests');
import createPMCmdt         = require('../test/dolphin/CreatePresentationModelCommandTests');
import cdt                  = require('../test/dolphin/ClientDolphinTests');
import cct                  = require('../test/dolphin/ClientConnectorTests');
import mt                   = require('../test/dolphin/MapTests');
import cmst                 = require('../test/dolphin/ClientModelStoreTests');
import codect               = require('../test/dolphin/CodecTest')


// new instance of tsUnit
var test = new tsUnit.tsUnit.Test();

// add your test class (you can call this multiple times)
test.addTestClass(new cat.dolphin.ClientAttributeTests(), "ClientAttributeTests");
test.addTestClass(new cpmt.dolphin.ClientPresentationModelTests(), "ClientPresentationModelTests");
test.addTestClass(new namedCmdt.dolphin.NamedCommandTests(), "NamedCommandTests");
test.addTestClass(new valChangedCmdt.dolphin.ValueChangedCommandTests(), "ValueChangedCommandTests");
test.addTestClass(new changedAttrMDCmdt.dolphin.ChangeAttributeMetadataCommandTests(), "ChangeAttributeMetadataCommandTests");
test.addTestClass(new emptyNt.dolphin.EmptyNotificationTests(), "EmptyNotificationTests");
test.addTestClass(new createPMCmdt.dolphin.CreatePresentationModelCommandTests(), "CreatePresentationModelCommandTests");
test.addTestClass(new cdt.dolphin.ClientDolphinTests(), "ClientDolphinTests");
test.addTestClass(new cct.dolphin.ClientConnectorTests(), "ClientConnector");
test.addTestClass(new mt.dolphin.MapTests(), "MapTests");
test.addTestClass(new cmst.dolphin.ClientModelStoreTests(), "ClientModelStoreTests");
test.addTestClass(new codect.dolphin.CodecTest(), "CodecTest");

// Use the built in results display
test.showResults(document.getElementById('results'), test.run());