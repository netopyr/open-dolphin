package org.opendolphin.core;

public class ModelStoreListenerWrapperTest extends GroovyTestCase {

    void testEquals() {
        ModelStoreListener listener = {} as ModelStoreListener
        def wrapper = new ModelStoreListenerWrapper('no-type', listener)
        assert wrapper == wrapper
        assert wrapper == new ModelStoreListenerWrapper('no-type', listener)
        assert wrapper != new Object()
        assert wrapper != new ModelStoreListenerWrapper('other-type', listener)
        assert wrapper != new ModelStoreListenerWrapper('no-type', {} as ModelStoreListener)
    }
}
