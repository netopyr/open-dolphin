package org.opendolphin.core.server.action

import org.opendolphin.core.ModelStore
import org.opendolphin.core.comm.PresentationModelResetedCommand
import org.opendolphin.core.comm.ResetPresentationModelCommand
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.comm.ServerConnector

class ResetPresentationModelActionTests extends GroovyTestCase {

    void testResetModel() {
        def store = new ModelStore()
        ServerConnector connector = new ServerConnector()
        ResetPresentationModelAction action = new ResetPresentationModelAction(store)
        store.add(new ServerPresentationModel('p1',[]))
        connector.register(action)
        List response = connector.receive(new ResetPresentationModelCommand('p1'))
        assert 1 == response.size()
        assert PresentationModelResetedCommand == response.first().class
        assert 'p1' == response.first().pmId

    }
}
