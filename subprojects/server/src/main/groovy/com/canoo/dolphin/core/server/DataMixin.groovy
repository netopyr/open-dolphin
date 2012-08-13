package com.canoo.dolphin.core.server

import groovy.transform.Canonical

class DataMixin {

    @Lazy
    protected LinkedList<Map.Entry> data

    Object putData(String key, Object value) {
        def oldEntry = data.find { it.key == key }
        if (oldEntry) data.remove(oldEntry)
        data << new Entry(key,value)
        return oldEntry?.value
    }

    Object findData(String key) {
        data.find { it.key == key }?.value
    }
}

@Canonical class Entry{
    final String key
    final Object value
}
