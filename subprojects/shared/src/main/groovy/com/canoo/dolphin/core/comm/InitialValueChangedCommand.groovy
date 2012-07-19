package com.canoo.dolphin.core.comm

class InitialValueChangedCommand extends Command {
    long attributeId

    String toString() { super.toString() + " attr:$attributeId"}
}