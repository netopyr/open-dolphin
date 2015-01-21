package org.opendolphin.core.client;

public class ClientPresentationModelTest extends GroovyTestCase{

    void testStandardCtor() {
        def model = new GClientPresentationModel('x',[])
        assert model.id == 'x'
    }
    void testNullIdCtor() {
        def model1 = ClientPresentationModelFactory.create([])
        def model2 = ClientPresentationModelFactory.create([])
        assert model1.id != model2.id
    }
    void testBadIdCtor() {
        shouldFail(IllegalArgumentException) {
            new GClientPresentationModel("1000-AUTO-CLT",[])
        }
    }
}
