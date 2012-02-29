package org.vafer.jmx.output;

import org.vafer.jmx.formatter.DefaultFormatter;

import javax.management.ObjectName;
import java.io.IOException;

public final class ConsoleOutput implements Output {

    public void open() throws IOException {
    }

    public void output(String node, String key, Number value) throws IOException {
        System.out.println(String.format("%s: %s = %s",
            node,
            key,
            value.toString()
        ));
    }
    public void close() throws IOException {
    }

}
