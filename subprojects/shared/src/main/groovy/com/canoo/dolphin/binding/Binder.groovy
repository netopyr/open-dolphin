package com.canoo.dolphin.binding

class Binder {
    // todo: also allow to bind against 'enabled', 'dirty', ...
    static OfAble bind(String propName) {
        new OfAble(propName)
    }


    static PojoOfAble bindInfo(String propName) {
        new PojoOfAble(propName)
    }
}
