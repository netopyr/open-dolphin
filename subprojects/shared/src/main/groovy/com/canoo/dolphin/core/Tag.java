package com.canoo.dolphin.core;

/**
 * An Attribute has a value and additional info like base value, isDirty, and potentially more (e.g. type).
 * Beside this, an Attribute can bear one of the tags that the Tag enum provides.
 * The default value attribute bears the VALUE tag. Lets assume it also has the qualifier 'x'.
 * Then there may be more attributes with the 'x' qualifier and one of the Tag tags to represent e.g.
 * whether the 'x' Attribute should be considered visible, enabled, and else.
 * The UI toolkit can bind against these "tag attributes" just like against the "value" attribute.
 */
public enum Tag {
    VALUE,
    LABEL,      // the to-be-displayed String, not the key. I18N happens on the server.
    ENABLED,    // boolean
    VISIBLE,    // boolean, maps to Grails constraint display:true
    OPTIONAL,   // boolean, maps to Grails constraint nullable:true
    TOOLTIP,    // String
    HELP_URL,   // Url.toExternalForm()
    REGEX,              // regular expression for local, syntactical constraints like in "rejectField"
    VALIDATION_ERROR,   // a single text; there may be many of such attributes

    // maybe this should be application specific
    ERROR_PM_ID,        // points to a PM that has more error info, e.g text and severity; there may be many of such attributes;
}
