package com.canoo.dolphin.core.server

public class ServerDolphinTest extends GroovyTestCase{

    ServerDolphin dolphin

    @Override
    protected void setUp() throws Exception {
        dolphin = new ServerDolphin()
    }

    // todo dk: creating a SPM adds the respective commands to the response

    void testPutToPmAndFindData() {
        def pm = new ServerPresentationModel("uniqueId", [])
        assert null    == dolphin.putData(pm, "key", "value")
        assert "value" == dolphin.findData(pm, "key")
        assert "value" == dolphin.putData(pm, "key", "otherValueForSameKey")
        assert "otherValueForSameKey" == dolphin.findData(pm, "key")
    }

    void testPutToAttributeAndFindData() {
        def att = new ServerAttribute("propName")
        assert null    == dolphin.putData(att, "key", "value")
        assert "value" == dolphin.findData(att, "key")
        assert "value" == dolphin.putData(att, "key", "otherValueForSameKey")
        assert "otherValueForSameKey" == dolphin.findData(att, "key")
    }

}
