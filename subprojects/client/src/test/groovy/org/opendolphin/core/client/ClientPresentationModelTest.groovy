package org.opendolphin.core.client;

public class ClientPresentationModelTest extends GroovyTestCase{

    void testStandardCtor() {
        def model = new ClientPresentationModel('x',[])
        assert model.id == 'x'
    }
    void testNullIdCtor() {
        def model1 = new ClientPresentationModel([])
        def model2 = new ClientPresentationModel([])
        assert model1.id != model2.id
    }
    void testBadIdCtor() {
        shouldFail(IllegalArgumentException) {
            new ClientPresentationModel("1000-AUTO-CLT",[])
        }
    }
}
