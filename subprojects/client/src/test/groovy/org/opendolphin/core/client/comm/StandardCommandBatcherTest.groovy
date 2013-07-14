package org.opendolphin.core.client.comm

class StandardCommandBatcherTest extends GroovyTestCase {

	CommandBatcher batcher

	@Override
	protected void setUp() throws Exception {
		batcher = new CommandBatcher()
	}

	void testEmpty() {
		assert batcher.isEmpty()
	}

	void testOne() {
		def cah = new CommandAndHandler()
		batcher.batch(cah)
        assert batcher.waitingBatches.val == [cah]
	}

	void testMultipleDoesNotBatch() {
		def list = [new CommandAndHandler()] * 3

		list.each { cwh -> batcher.batch(cwh) }

        assert batcher.waitingBatches.val == [list[0] ]
        assert batcher.waitingBatches.val == [list[1]]
        assert batcher.waitingBatches.val == [list[2]]
	}
}
