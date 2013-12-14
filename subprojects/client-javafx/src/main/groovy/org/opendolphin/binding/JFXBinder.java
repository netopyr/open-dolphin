package org.opendolphin.binding;

import org.opendolphin.core.Tag;

public class JFXBinder {

    public static JFXBindOfAble bind(String sourcePropertyName) {
        return new JFXBindOfAble(sourcePropertyName, Tag.VALUE);
    }

    public static JFXBindOfAble bind(String sourcePropertyName, Tag tag) {
        return new JFXBindOfAble(sourcePropertyName, tag);
    }

    public static BindPojoOfAble bindInfo(String sourcePropertyName) {
        return Binder.bindInfo(sourcePropertyName);
    }

    public static JFXUnbindOfAble unbind(String sourcePropertyName) {
        return new JFXUnbindOfAble(sourcePropertyName);
    }

    public static UnbindInfoOfAble unbindInfo(String sourcePropertyName) {
        return Binder.unbindInfo(sourcePropertyName);
    }
}
