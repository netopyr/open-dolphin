package com.canoo.dolphin.core.comm

import groovy.transform.TupleConstructor

@TupleConstructor
class InitializeAttributeCommand extends Command {

    String pmId
    String propertyName
    String dataId
    def    newValue

    String toString() { super.toString() + " pm '$pmId' property '$propertyName' initial value '$newValue' dataId $dataId"}
}
