package org.opendolphin.core.client.comm

import groovyx.gpars.dataflow.DataflowQueue

class CommandBatcher {

    final DataflowQueue<List<CommandAndHandler>> waitingBatches = new DataflowQueue<>()

	void batch(CommandAndHandler commandAndHandler) {
        waitingBatches << [commandAndHandler]
	}

	boolean isEmpty() {
		true
	}

}
