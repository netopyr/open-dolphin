package org.opendolphin.core.client.comm;

interface ICommandBatcher {
    void batch(CommandAndHandler commandAndHandler);
	boolean isEmpty();
}
