package com.canoo.dolphin.core;

/**
 * An Attribute has a value and additional info like base value, isDirty, and potentially more (e.g. type).
 * Beside this, an Attribute can bear one of the tags that the Tag enum provides.
 * The default value attribute bears the VALUE tag.
 * Then there may be more attributes for the same presentation model and property name with a Tag to represent e.g.
 * whether the Attribute should be considered visible, enabled, and else.
 * The UI toolkit can bind against these "tag attributes" just like against the "value" attribute.
 */
public enum Tag {
    VALUE,
    LABEL,      // the to-be-displayed String, not the key. I18N happens on the server.
    ENABLED,    // boolean
    VISIBLE,    // boolean, maps to Grails constraint display:true
    MANDATORY,  // boolean, maps to Grails constraint nullable:false
    TOOLTIP,    // String
    HELP_URL,   // Url.toExternalForm()
    REGEX,            // regular expression for local, syntactical constraints like in "rejectField"
    VALIDATION_MSG,   // a single text;

    // maybe this should be application specific
    ERROR_TYPE,       // PMs of this type capture the validation errors for this attribute
}
