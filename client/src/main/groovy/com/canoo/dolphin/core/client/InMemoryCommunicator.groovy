package com.canoo.dolphin.core.client

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import groovy.util.logging.Log

@Singleton @Log
class InMemoryCommunicator implements PropertyChangeListener {

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        log.info "sending -> ${evt.source} -> ${evt.newValue}"

        // todo: at this point we would
        // - trigger the "server side" PCLs
        // - listen to subsequent changes in the server Attributes
        // - promote these changes to the client side (with endless looping check)
        // Since client and server are identical for the InMemory case
        // the remaining work is to adapt the binding and controller setup,
        // which should be unaware of the special remoting choice
    }

}
