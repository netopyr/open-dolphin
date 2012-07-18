package com.canoo.dolphin.binding

class JFXBinder {
    static JFXOfAble bind(String propName) {
        new JFXOfAble(propName)
    }

    static PojoOfAble bindInfo(String propName) {
        Binder.bindInfo(propName)
    }
}
