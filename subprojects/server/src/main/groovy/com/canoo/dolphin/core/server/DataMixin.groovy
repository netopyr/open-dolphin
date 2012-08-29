package com.canoo.dolphin.core.server

import groovy.transform.Canonical

class DataMixin {

    @Lazy
    protected LinkedList<DataEntry> data

    Object putData(String key, Object value) {
        def oldEntry = data.find { it.key == key }
        if (oldEntry) data.remove(oldEntry)
        data << new DataEntry(key,value)
        return oldEntry?.value
    }

    Object findData(String key) {
        data.find { it.key == key }?.value
    }

    Object removeData(String key) {
        def oldEntry = data.find { it.key == key }
        if (oldEntry) data.remove(oldEntry)
        return oldEntry?.value
    }

    List<String> getDataKeys() {
        List<String> keys = []
        keys.addAll(data.key)
        keys
    }
}

@Canonical class DataEntry {
    final String key
    final Object value
}
