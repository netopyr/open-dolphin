package com.canoo.dolphin.core

import spock.lang.Specification

class ModelStoreSpec extends Specification {
    def "pmid is unique"() {
        given:

        def pmA = new BasePresentationModel('myId', [])
        def pmB = new BasePresentationModel('myId', [])

		def modelStore = new ModelStore()

		when:

		modelStore.add(pmA)
		modelStore.add(pmB)

        then:

		thrown(IllegalArgumentException)
    }

}