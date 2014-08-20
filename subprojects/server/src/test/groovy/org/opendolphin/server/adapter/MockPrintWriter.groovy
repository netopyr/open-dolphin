package org.opendolphin.server.adapter

public class MockPrintWriter extends PrintWriter {
    MockPrintWriter() throws FileNotFoundException {
        super(new StringWriter())
    }

    @Override
    void write(String s) {
        // ignore
    }
}
