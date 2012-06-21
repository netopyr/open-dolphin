package com.canoo.dolphin.core.comm

class InitializeSharedAttributeCommand extends Command {

    String pmId
    String propertyName

    String sharedPmId
    String sharedPropertyName

    def  newValue // will only be used if the source value is not yet available (?)

    String toString() { super.toString() + " pm '$pmId' property '$propertyName' initial value '$newValue' sharedPM '$sharedPmId' sharedProperty '$sharedPropertyName' "}
}
