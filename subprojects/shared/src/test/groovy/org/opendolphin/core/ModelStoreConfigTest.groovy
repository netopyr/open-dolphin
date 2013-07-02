package org.opendolphin.core

import java.util.logging.Level

class ModelStoreConfigTest extends GroovyLogTestCase {

    private ModelStoreConfig modelStoreConfig

    @Override
    void setUp() {
        modelStoreConfig = new ModelStoreConfig()
    }

    void testDefaultCapacitiesPowerOfTwo() {
        // no warn message should be logged
        assert getLog {new ModelStoreConfig()}.isEmpty()
    }

    void testAttributeCapacity() {
        // no warn message should be logged
        assert getLog {modelStoreConfig.setAttributeCapacity(4)}.isEmpty()
        assert 4 == modelStoreConfig.getAttributeCapacity()

        // a warn message should be logged
        assert getLog {modelStoreConfig.setAttributeCapacity(5)}.contains('attributeCapacity')
        assert 5 == modelStoreConfig.getAttributeCapacity()
    }

    void testSetPmCapacity() {
        // no warn message should be logged
        assert getLog {modelStoreConfig.setPmCapacity(4)}.isEmpty()
        assert 4 == modelStoreConfig.getPmCapacity()

        // a warn message should be logged
        assert getLog {modelStoreConfig.setPmCapacity(5)}.contains('pmCapacity')
        assert 5 == modelStoreConfig.getPmCapacity()
    }

    void testQualifierCapacity() {
        // no warn message should be logged
        assert getLog {modelStoreConfig.setQualifierCapacity(4)}.isEmpty()
        assert 4 == modelStoreConfig.getQualifierCapacity()

        // a warn message should be logged
        assert getLog {modelStoreConfig.setQualifierCapacity(5)}.contains('qualifierCapacity')
        assert 5 == modelStoreConfig.getQualifierCapacity()
    }

    void testTypeCapacity() {
        // no warn message should be logged
        assert getLog {modelStoreConfig.setTypeCapacity(4)}.isEmpty()
        assert 4 == modelStoreConfig.getTypeCapacity()

        // a warn message should be logged
        assert getLog {modelStoreConfig.setTypeCapacity(5)}.contains('typeCapacity')
        assert 5 == modelStoreConfig.getTypeCapacity()
    }


    private static String getLog(Closure inClosure) {
        return stringLog(Level.WARNING, ModelStoreConfig.class.getName(), inClosure)
    }
}
