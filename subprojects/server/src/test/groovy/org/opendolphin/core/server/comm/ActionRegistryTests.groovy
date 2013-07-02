package org.opendolphin.core.server.comm

import org.opendolphin.core.comm.AttributeCreatedNotification
import org.opendolphin.core.comm.DataCommand

class ActionRegistryTests extends GroovyTestCase {
    ActionRegistry registry

    @Override
    protected void setUp() throws Exception {
        registry = new ActionRegistry()
    }

    void testRegisterCommand() {
        assert 0 == registry.actions.size()
        def firstAction = {}
        registry.register('Data', firstAction)
        assert 1 == registry.getAt('Data').size()
        assert registry.getAt('Data').closure.contains(firstAction)

        def otherAction = {}
        registry.register(DataCommand, otherAction)
        registry.register(AttributeCreatedNotification, otherAction)
        assert 2 == registry.actions.size()
        assert 2 == registry.getAt('Data').size()
        assert 1 == registry.getAt('AttributeCreated').size()
    }

    void testRegisterCommandHandler(){
        TestSimpleCommandHandler commandHandler = new TestSimpleCommandHandler()
        registry.register('Data', commandHandler)
        assert registry.getAt('Data').contains(commandHandler)
        registry.register(DataCommand, new TestSimpleCommandHandler())
        assert 1 == registry.actions.size()
        assert 2 == registry.getAt('Data').size()
    }

    void testUnregisterCommandHandler() {
        TestSimpleCommandHandler commandHandler = new TestSimpleCommandHandler()
        registry.register('Data',commandHandler)
        assert 1 == registry.getAt('Data').size()
        registry.unregister('Data',commandHandler)
        assert 0 == registry.getAt('Data').size()
        registry.register(DataCommand, commandHandler)
        assert 1 == registry.getAt('Data').size()
        registry.unregister(DataCommand,commandHandler)
        assert 0 == registry.getAt('Data').size()
    }

    void testUnregisterCommand() {
        def action = {}
        registry.register('Data',action)
        assert 1 == registry.getAt('Data').size()
        registry.unregister('Data',action)
        assert 0 == registry.getAt('Data').size()
        registry.register(DataCommand, action)
        assert 1 == registry.getAt('Data').size()
        registry.unregister(DataCommand,action)
        assert 0 == registry.getAt('Data').size()
    }

    void testRegisterCommand_MultipleCalls() {
        assert 0 == registry.actions.size()
        def action = {}
        registry.register('Data', action)
        assert 1 == registry.getAt('Data').size()

        registry.register('Data', action)
        assert 1 == registry.getAt('Data').size()
    }

}
