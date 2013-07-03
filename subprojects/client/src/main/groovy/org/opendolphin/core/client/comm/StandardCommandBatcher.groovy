package org.opendolphin.core.client.comm

import groovyx.gpars.agent.Agent

class StandardCommandBatcher implements ICommandBatcher {

	protected Agent<LinkedList<CommandAndHandler>> agent = Agent.agent(new LinkedList<CommandAndHandler>())

	/** takes the available List<CommandAndHandler> as argument */
	Closure handleCommands

	@Override
	void batch(CommandAndHandler commandAndHandler) {
		agent << { List<CommandAndHandler> safeQueue ->
			safeQueue << commandAndHandler
		}
		update()
	}

	protected void update() {
		agent << { List<CommandAndHandler> safeQueue ->
			handleCommands([unshift(safeQueue)])
		}
	}

	@Override
	boolean isEmpty() {
		agent.val.empty
	}

	protected CommandAndHandler unshift(List<CommandAndHandler> intern) {
		CommandAndHandler first = intern.empty ? null : intern.first()
		if (first) intern.remove(first)
		return first
	}


}
