package org.vafer.jmx.pipe;

import org.vafer.jmx.JmxQuery;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import java.io.IOException;

public interface JmxPipe {

    public void open() throws IOException;
    public void output(String node, JmxQuery.JmxAttribute metric) throws IOException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException;
    public void close() throws IOException;

}
