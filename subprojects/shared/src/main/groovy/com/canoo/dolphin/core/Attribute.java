package com.canoo.dolphin.core;

public interface Attribute extends Observable {
    String DATA_ID_PROPERTY = "dataId";

    String DIRTY_PROPERTY = "dirty";

    String INITIAL_VALUE = "initialValue";

    Object getValue();

    void setValue(Object value);

    String getPropertyName();

    String getDataId();

    long getId();

    void setId(long id);

    void syncWith(Attribute source);

    boolean isDirty();

    Object getInitialValue();

    void save();
}