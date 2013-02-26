package org.opendolphin.core.server

import org.opendolphin.core.Tag
import groovy.transform.CompileStatic

@CompileStatic
class Slot {
    String propertyName
    Object value
    String qualifier
    Tag    tag

    /**
     * Convenience method with positional parameters to create an attribute specification from name/value pairs.
     * Especially useful when creating DTO objects.
     */
    Slot (String propertyName, Object value, String qualifier = null, Tag tag = Tag.VALUE) {
        this.propertyName = propertyName
        this.value= value
        this.qualifier =  qualifier
        this.tag = tag
    }

    /**
     * Converts a data map like <tt>[a:1, b:2]</tt> into a list of attribute-Maps.
     * Especially useful when a service returns data that an action puts into presentation models.
     */
    static List<Slot> slots(Map<String, Object> data) {
        data.collect(new LinkedList()) { String key, Object value -> new Slot(key, value) }
    }


    Map<String, Object> toMap() {
        [propertyName: propertyName, value: value, qualifier: qualifier, tag:tag]
    }

}
