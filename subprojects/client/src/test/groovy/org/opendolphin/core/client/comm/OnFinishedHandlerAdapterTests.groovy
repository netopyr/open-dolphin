package org.opendolphin.core.client.comm

class OnFinishedHandlerAdapterTests extends GroovyTestCase{

    void testAdapter() {
        new OnFinishedHandlerAdapter().onFinished([])
        new OnFinishedHandlerAdapter().onFinishedData([[:]])
    }
}
