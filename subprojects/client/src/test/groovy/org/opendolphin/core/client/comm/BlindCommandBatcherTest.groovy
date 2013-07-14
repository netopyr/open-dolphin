package org.opendolphin.core.client.comm

class BlindCommandBatcherTest extends GroovyTestCase {

    BlindCommandBatcher batcher

    @Override
    protected void setUp() throws Exception {
        batcher = new BlindCommandBatcher()
    }

    void testMultipleBlindsAreBatched() {
        assert batcher.isEmpty()
        batcher.deferMillis = 50
        def list = [new CommandAndHandler(), new CommandAndHandler(), new CommandAndHandler()]

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }
        batcher.waitingBatches.val == list
    }

    void testNonBlindForcesBatch() {
        assert batcher.isEmpty()
        batcher.deferMillis = 50
        def list = [new CommandAndHandler(), new CommandAndHandler(), new CommandAndHandler()]
        list << new CommandAndHandler(handler: new OnFinishedHandlerAdapter())

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }
        batcher.waitingBatches.val == list[0..2]
        batcher.waitingBatches.val == list[3]
    }

    // todo dk: more tests for the maxBatchSize
}
