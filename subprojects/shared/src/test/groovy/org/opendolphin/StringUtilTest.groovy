package org.opendolphin

import static org.opendolphin.StringUtil.isBlank

public class StringUtilTest extends GroovyTestCase {

    void testIsBlank() {
        assert isBlank(null)
        assert isBlank('')
        assert isBlank(' ')
        assert isBlank("\t")
        assert isBlank(" \t\n")
        assert !isBlank('a')
        assert !isBlank('.')
    }
}
