package com.canoo.dolphin.core.comm

import groovy.transform.TupleConstructor

@TupleConstructor
class InitializeAttributeCommand extends Command {

    String pmId
    String propertyName
    String qualifier
    def    newValue
    String pmType

    String toString() { super.toString() + " pm '$pmId' pmType'$pmType' property '$propertyName' initial value '$newValue' qualifier $qualifier"}
}
