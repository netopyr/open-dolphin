package org.opendolphin.core

import org.opendolphin.core.client.ClientDolphinFactory
import spock.lang.Specification

class NoModelStoreTest extends Specification {

    void "calling the no-model store stores no models"() {
        given:
        def modelStore = new NoModelStore(ClientDolphinFactory.create());
        when:
        def added = modelStore.add(null)
        then:
        added == false
        modelStore.listPresentationModels().size() == 0
        when:
        def removed = modelStore.remove(null)
        then:
        removed == false
    }
}
