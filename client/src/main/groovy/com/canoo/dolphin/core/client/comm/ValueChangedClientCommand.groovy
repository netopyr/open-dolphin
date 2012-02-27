package com.canoo.dolphin.core.client.comm

class ValueChangedClientCommand extends ClientCommand {

    long attributeId

    def  oldValue
    def  newValue

    String toString() { super.toString() + " attr:$attributeId old:$oldValue new:$newValue"}
}
