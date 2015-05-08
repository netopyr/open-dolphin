package org.opendolphin.core.server

import groovy.transform.CompileStatic

//CompileStatic
class DTO {
    List<Slot> slots

    DTO(List<Slot> newSlots) {
        slots = newSlots
    }

    DTO(Slot... newSlots) {
        slots = newSlots as LinkedList

    }

    /**
     * Create the representation that is used within commands.
     */
    List<Map<String, Object>> encodable() {
        (List<Map<String, Object>>) slots.collect(new LinkedList()) {Slot slot -> slot.toMap() }
    }

}
