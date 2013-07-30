/*
 * Copyright 2012-2013 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.binding;

import org.junit.Test;
import org.opendolphin.core.AbstractObservable;
import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.BasePresentationModel;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Java Style Tests for Binding
 */
public class BinderJavaTest {

    private final String initialValue = "initialValue";
    private final String newValue = "newValue";

    @Test
    public void testPojoBinding() {
        TestPojo sourcePojo = new TestPojo();
        sourcePojo.setValue(initialValue);

        TestPojo targetPojo = new TestPojo();
        assertEquals(null, targetPojo.getValue());

        Binder.bind("value").of(sourcePojo).to("value").of(targetPojo);

        assertEquals(initialValue, targetPojo.getValue());

        sourcePojo.setValue(newValue);

        assertEquals(newValue, targetPojo.getValue());
    }

    @Test
    public void testPojoBindingWithConverter() {
        TestPojo sourcePojo = new TestPojo();
        sourcePojo.setValue(initialValue);

        TestPojo targetPojo = new TestPojo();
        assertEquals(null, targetPojo.getValue());

        Converter converter = new Converter() {
            @Override
            public Object convert(Object value) {
                return "[" + value + "]";
            }
        };

        Binder.bind("value").of(sourcePojo).to("value").of(targetPojo, converter);

        assertEquals("[initialValue]", targetPojo.getValue());

        sourcePojo.setValue(newValue);

        assertEquals("[newValue]", targetPojo.getValue());
    }

    @Test
    public void testAttributeBinding() {
        BasePresentationModel sourcePm = new BasePresentationModel("1", Arrays.asList(new TestAttribute("text")));
        sourcePm.getAt("text").setValue(initialValue);

        TestPojo targetPojo = new TestPojo();

        assertEquals(null, targetPojo.getValue());

        Binder.bind("text").of(sourcePm).to("value").of(targetPojo);

        assertEquals(initialValue, targetPojo.getValue());

        sourcePm.getAt("text").setValue(newValue);

        assertEquals(newValue, targetPojo.getValue());
    }


    @Test
    public void testAttributeBindingWithConverter() {
        BasePresentationModel sourcePm = new BasePresentationModel("1", Arrays.asList(new TestAttribute("text")));
        sourcePm.getAt("text").setValue(initialValue);

        TestPojo targetPojo = new TestPojo();

        Converter converter = new Converter() {
            @Override
            public Object convert(Object value) {
                return "[" + value + "]";
            }
        };

        assertEquals(null, targetPojo.getValue());

        Binder.bind("text").of(sourcePm).to("value").of(targetPojo, converter);

        assertEquals("[initialValue]", targetPojo.getValue());

        sourcePm.getAt("text").setValue(newValue);

        assertEquals("[newValue]", targetPojo.getValue());
    }

    // Binding support for Java classes is established by implementing Observable
    private static class TestPojo extends AbstractObservable {

        private String value;

        public void setValue(String newValue) {
            String oldValue = value;
            value = newValue;
            firePropertyChange("value", oldValue, newValue);
        }

        public String getValue() {
            return value;
        }

    }

    private static class TestAttribute extends BaseAttribute {

        TestAttribute(String propertyName) {
            super(propertyName);
        }

    }
}

