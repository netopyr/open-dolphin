package com.canoo.dolphin.core.comm

class ValueChangedCommand extends Command {

    long attributeId

    def  oldValue
    def  newValue

    String toString() { super.toString() + " attr:$attributeId, $oldValue -> $newValue"}
}
