package org.vafer.jmx.output;

import org.vafer.jmx.formatter.DefaultFormatter;

import javax.management.ObjectName;

public final class NagiosOutput implements Output {

    public void open() {
    }

    public void output(String node, String key, Number value) {
        // System.out.println(node + "." + key + " = " + value);
        // JMX OK ObjectPendingFinalizationCount=0
    }

    public void close() {
    }

}