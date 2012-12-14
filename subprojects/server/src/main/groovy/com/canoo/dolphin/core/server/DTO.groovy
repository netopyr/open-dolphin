package com.canoo.dolphin.core.server

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
        slots.collect(new LinkedList()) { it.toMap() }
    }

}
