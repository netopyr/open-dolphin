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

    void testListPresentationModels() {
        assert dolphin.listPresentationModelIds().empty
        assert dolphin.listPresentationModels().empty

        def pm1 = new ServerPresentationModel("first", [])
        dolphin.modelStore.add pm1

        assert ['first'].toSet() == dolphin.listPresentationModelIds()
        assert [pm1]             == dolphin.listPresentationModels().toList()

        def pm2 = new ServerPresentationModel("second", [])
        dolphin.modelStore.add pm2

        assert 2 == dolphin.listPresentationModelIds().size()
        assert 2 == dolphin.listPresentationModels().size()

        for (id in dolphin.listPresentationModelIds()){
            assert dolphin.findPresentationModelById(id) in dolphin.listPresentationModels()
        }
    }

}
