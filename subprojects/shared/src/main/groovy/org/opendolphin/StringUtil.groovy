package org.opendolphin

class StringUtil {

    /**
     * <p>Determines whether a given string is <code>null</code>, empty,
     * or only contains whitespace. If it contains anything other than
     * whitespace then the string is not considered to be blank and the
     * method returns <code>false</code>.</p>
     *
     * @param str The string to test.
     * @return <code>true</code> if the string is <code>null</code>, or
     *         blank.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0
    }
}
