package org.opendolphin.server.adapter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MockPrintWriter extends PrintWriter {
    MockPrintWriter() throws FileNotFoundException {
        super(new StringWriter());
    }

    @Override
    public void write(String s) {
        // ignore
    }
}
