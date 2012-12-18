/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.core;

import groovy.lang.GString;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The value may be null as long as the BaseAttribute is used as a "placeholder".
 */

public abstract class BaseAttribute extends AbstractObservable implements Attribute {

    static final public  Class[] SUPPORTED_VALUE_TYPES = {Character.class, String.class, Number.class, Boolean.class, Date.class};
    static final private Logger  log                   = Logger.getLogger(BaseAttribute.class.getName());

    private final String propertyName;
    private       Object value;
    private       Object baseValue;
    private boolean dirty = false;
    private final Tag   tag;

    private long id = System.identityHashCode(this); // todo: dk: has to change to tell client from server
    private String qualifier; // application specific semantics apply

    public BaseAttribute(String propertyName) {
        this(propertyName, null);
    }

    public BaseAttribute(String propertyName, Object baseValue) {
        this(propertyName, baseValue, Tag.VALUE);
    }

    public BaseAttribute(String propertyName, Object baseValue, Tag tag) {
        this.propertyName = propertyName;
        this.baseValue = baseValue;
        this.value = baseValue;
        this.tag = tag;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Object getBaseValue() {
        return baseValue;
    }

    public Object getValue() {
        return value;
    }

    public Tag getTag() {
        return tag;
    }

    /** Check whether value is of allowed type and convert to an allowed type if possible. */
    static Object checkValue(Object value) {
        if (null == value) return null;
        Object result = value;
        if (result instanceof GString) result = value.toString();
        if (result instanceof BaseAttribute) {
            if (log.isLoggable(Level.WARNING)) log.warning("An Attribute may not itself contain an attribute as a value. Assuming you forgot to call getValue().");
            result = checkValue((((BaseAttribute) value).getValue()));
        }
        boolean ok = false;
        for (Class type : SUPPORTED_VALUE_TYPES ) {
            if (type.isAssignableFrom(result.getClass())) { ok = true; break; }
        }
        if (!ok) {
            throw new IllegalArgumentException("Attribute values of this type are not allowed: " + result.getClass().getSimpleName());
        }
        return result;
    }

    // todo dk: think about specific method versions for each allowed type
    public void setValue(Object value) {
        value = checkValue(value);
        setDirty(baseValue == null ? value != null : !baseValue.equals(value));
        firePropertyChange(VALUE, this.value, this.value = value);
    }

    private void setDirty(boolean dirty) {
        firePropertyChange(DIRTY_PROPERTY, this.dirty, this.dirty = dirty);
    }

    private void setBaseValue(Object baseValue) {
        setDirty(baseValue == null ? value != null : !baseValue.equals(value));
        firePropertyChange(BASE_VALUE, this.baseValue, this.baseValue = baseValue);
    }

    public void rebase() {
        setBaseValue(getValue());
    }

    public void reset() {
        setValue(getBaseValue());
        setDirty(false);
    }

    public String toString() {
        return new StringBuilder()
                .append(id)
                .append(" : ")
                .append(propertyName)
                .append(" (")
                .append(qualifier).append(") ")
                .append("[").append(tag.name()).append("] ")
                .append(value).toString();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        firePropertyChange(QUALIFIER_PROPERTY, this.qualifier, this.qualifier = qualifier);
    }

    public void syncWith(Attribute source) {
        if (this == source || null == source) return;
        setBaseValue(source.getBaseValue());
        setQualifier(source.getQualifier());
        setValue(source.getValue());
    }
}
