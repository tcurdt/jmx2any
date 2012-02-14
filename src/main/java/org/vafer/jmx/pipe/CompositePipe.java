package org.vafer.jmx.pipe;

import org.vafer.jmx.JmxQuery;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import java.io.IOException;
import java.util.Collection;

public final class CompositePipe implements JmxPipe {

    private final JmxPipe[] outputs;

    public CompositePipe(JmxPipe... outputs) {
        this.outputs = outputs;
    }

    public CompositePipe(Collection<JmxPipe> outputs) {
        this(outputs.toArray(new JmxPipe[outputs.size()]));
    }

    public void open() throws IOException {
        for(JmxPipe output : outputs) {
            output.open();
        }
    }

    public void output(String node, JmxQuery.JmxAttribute metric) throws IOException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
        for(JmxPipe output : outputs) {
            output.output(node, metric);
        }
    }


    public void close() throws IOException {
        for(JmxPipe output : outputs) {
            output.close();
        }
    }
}
