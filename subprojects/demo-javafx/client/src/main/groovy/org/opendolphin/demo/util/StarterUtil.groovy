package org.opendolphin.demo.util

import java.lang.reflect.Field

class StarterUtil {

    public static macFontWorkaround() {
        // Mac-specific hack for java 7 on el capitan
        try {
            Class<?> macFontFinderClass = Class.forName("com.sun.t2k.MacFontFinder");
            Field psNameToPathMap = macFontFinderClass.getDeclaredField("psNameToPathMap");
            psNameToPathMap.setAccessible(true);
            psNameToPathMap.set(null, new HashMap<String, String>());
        } catch (Exception e) {
            // ignore
        }
    }
}
