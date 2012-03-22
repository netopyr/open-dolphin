package com.canoo.dolphin.core.comm

class AttributeCreatedCommand extends Command {

    String  pmId
    long    attributeId
    String  propertyName

    // todo dk: possible optimization: provide initial value


    String toString() { super.toString() + " attr:$attributeId, pm:$pmId, property:$propertyName"}

}
