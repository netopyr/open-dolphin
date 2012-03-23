package com.canoo.dolphin.core.comm

class InitializeAttributeCommand extends Command {

    String pmId
    String propertyName

    def  newValue

    String toString() { super.toString() + " pm '$pmId' property '$propertyName' initial value '$newValue'"}
}
