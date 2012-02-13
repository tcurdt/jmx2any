package org.vafer.jmx.output;

import javax.management.ObjectName;
import java.io.IOException;

public interface Output {

    public void open() throws IOException;
    public void output(String node, String key, Number value) throws IOException;
    public void close() throws IOException;

}
