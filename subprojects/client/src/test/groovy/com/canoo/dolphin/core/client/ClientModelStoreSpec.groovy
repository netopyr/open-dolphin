package com.canoo.dolphin.core.client

import spock.lang.Specification
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

/**
 * @author Dieter Holz
 */
class ClientModelStoreSpec extends Specification {
	def modelStore, pmType, pm, listener

	def setup(){
		modelStore = new ClientModelStore()
		Dolphin.clientModelStore = modelStore
		Dolphin.clientConnector = InMemoryClientConnector.instance

		pmType = 'myType'
		pm = new ClientPresentationModel('myId', [])
		pm.setPresentationModelType(pmType)

		listener = Mock(PresentationModelListChangedListener)
		modelStore.onPresentationModelListChanged(pmType, listener)
	}

	void "listeners are notified if PM is added to the clientModelStore"() {
		when:
		modelStore.add(pm)

		then:
		1 * listener.added(pm)
		0 * listener.removed(_)
	}

	void "listeners are notified if PM is removed from clientModelStore"() {
		given:
		modelStore.add(pm)

		when:
		modelStore.remove(pm)

		then:
		0 * listener.added(pm)
		1 * listener.removed(pm)
	}

	void "listeners are not notified for different pmTypes"() {
		given:
		def otherPm = new ClientPresentationModel('otherId', [])
		otherPm.setPresentationModelType('otherType')

		when:
		modelStore.add(otherPm)
		modelStore.remove(otherPm)

		then:
		0 * listener.added(pm)
		0 * listener.removed(pm)
	}

}
