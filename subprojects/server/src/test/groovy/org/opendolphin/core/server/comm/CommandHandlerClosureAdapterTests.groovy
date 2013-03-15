package org.opendolphin.core.server.comm

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.NamedCommand

class CommandHandlerClosureAdapterTests extends GroovyTestCase {

    void testEqualsAndHashcodeImplementation() {
        def closureOne = { Command cmd, List<Command> response -> response << new NamedCommand('result')}
        def closureTwo = { Command cmd, List<Command> response -> }
        CommandHandlerClosureAdapter adapter = new CommandHandlerClosureAdapter(closureOne)
        assert adapter.equals(new CommandHandlerClosureAdapter(closureOne))
        assert adapter.equals(adapter)
        assert !adapter.equals(new CommandHandlerClosureAdapter(closureTwo))
        assert !adapter.equals('')
        assert !adapter.equals(null)
        assert adapter.hashCode() == closureOne.hashCode()
    }

    void testCallClosure() {
        def closureOne = { Command cmd, List<Command> response -> response << new NamedCommand('result')}
        CommandHandlerClosureAdapter adapter = new CommandHandlerClosureAdapter(closureOne)
        def response = []
        adapter.handleCommand(new NamedCommand('cmd'), response)
        assert 1 == response.size()
        assert 'result' == response.first().id
        assert closureOne == adapter.getClosure()
    }


}
