package org.opendolphin.core.client.comm

import groovyx.gpars.group.DefaultPGroup

import java.util.concurrent.CountDownLatch

class StandardCommandBatcherTest extends GroovyTestCase {

	private static final DefaultPGroup group = new DefaultPGroup()
	StandardCommandBatcher batcher

	@Override
	protected void setUp() throws Exception {
		batcher = new StandardCommandBatcher()
	}

	void testEmpty() {
		assert batcher.isEmpty()
	}

	void testOne() {
		assert batcher.isEmpty()
		def cah = new CommandAndHandler()
		CountDownLatch latch = new CountDownLatch(1)
		batcher.handleCommands = { List<CommandAndHandler> batch ->
			group.task {
				assert batch == [cah]
				latch.countDown()
			}
		}
		batcher.batch(cah)
		latch.await()
		assert batcher.isEmpty()
	}

	void testMultipleDoesNotBatch() {
		assert batcher.isEmpty()
		def list = [new CommandAndHandler()] * 3

		CountDownLatch latch = new CountDownLatch(1)
		batcher.handleCommands = { batch ->
			group.task {
				assert batch.size() == 1
				if (batch.first() == list.last()) latch.countDown()
			}
		}
		list.each { cwh -> batcher.batch(cwh) }
		latch.await()
		assert batcher.isEmpty()
	}
}
