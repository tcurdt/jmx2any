package org.vafer.jmx.output;

import org.vafer.jmx.JmxQuery;
import org.vafer.jmx.formatter.DefaultFormatter;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import java.io.IOException;
import java.util.Set;

public final class OutputFilter implements JmxPipe {

    private final static DefaultFormatter formatter = new DefaultFormatter();
    
    private final JmxPipe output;
    private final Set<String> metrics;

    public OutputFilter(JmxPipe output, Set<String> metrics) {
        this.output = output;
        this.metrics = metrics;
    }

    public void open() throws IOException {
        output.open();
    }

    public void output(String node, JmxQuery.JmxAttribute metric) throws IOException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
        final String name = formatter.attributeName(metric.getBeanName(),metric.getAttributeName());
        if (metrics.contains(name)) {
            output.output(node, metric);
        } else {
            // System.out.println("filtering out: " + name);
        }
    }

    public void close() throws IOException {
        output.close();
    }
}
