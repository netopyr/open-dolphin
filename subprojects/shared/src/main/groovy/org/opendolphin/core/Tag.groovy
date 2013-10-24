package org.opendolphin.core

/**
 * An Attribute has a value and additional info like base value, isDirty, and potentially more (e.g. type).
 * Beside this, an Attribute can bear one of the tags that the Tag class provides.
 * The default value attribute bears the VALUE tag.
 * Then there may be more attributes for the same presentation model and property name with a Tag to represent e.g.
 * whether the Attribute should be considered visible, enabled, and else.
 * The UI toolkit can bind against these "tag attributes" just like against the "value" attribute.
 *
 * Tags are essentially statically typed Strings and you can make your own by subclassing.
 */
class Tag {

    final String name;

    protected Tag(String name) { this.name = name }

    public final String toString() { name }

    /** Factory method with flyweight pattern */
    public static final Map<String,Tag> tagFor = [:].withDefault { String key -> new Tag(key) }

    /** The actual value of the attribute. This is the default if no tag is given.*/
    public static final Tag VALUE = tagFor["VALUE"]

    /** the to-be-displayed String, not the key. I18N happens on the server. */
    public static final Tag LABEL = tagFor["LABEL"]

    /** a single text; e.g. "textArea" if the String value should be displayed in a text area instead of a textField */
    public static final Tag WIDGET_HINT = tagFor["WIDGET_HINT"]

    /** a single text; e.g. "java.util.Date" if the value String represents a date */
    public static final Tag VALUE_TYPE = tagFor["VALUE_TYPE"]

    /** regular expression for local, syntactical constraints like in "rejectField" */
    public static final Tag REGEX = tagFor["REGEX"]

    /** Url.toExternalForm()*/
    public static final Tag HELP_URL = tagFor["HELP_URL"]

    public static final Tag TOOLTIP = tagFor["TOOLTIP"]

    /** "true" or "false", maps to Grails constraint nullable:false */
    public static final Tag MANDATORY = tagFor["MANDATORY"]

    /** "true" or "false", maps to Grails constraint display:true */
    public static final Tag VISIBLE = tagFor["VISIBLE"]

    /** "true" or "false" */
    public static final Tag ENABLED = tagFor["ENABLED"]
}
