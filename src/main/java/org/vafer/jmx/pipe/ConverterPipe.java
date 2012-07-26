package org.vafer.jmx.pipe;

import org.vafer.jmx.JmxQuery;
import org.vafer.jmx.formatter.DefaultFormatter;
import org.vafer.jmx.output.Enums;
import org.vafer.jmx.output.Output;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;

public final class ConverterPipe implements JmxPipe {

    private final static DefaultFormatter formatter = new DefaultFormatter();

    private final Output output;
    private final String prefix;
    private final Enums enums;

    public ConverterPipe(Output output, String prefix, Enums enums) {
        this.output = output;
        this.prefix = prefix;
        this.enums = enums;
    }

    public void open() throws IOException {
        output.open();
    }

    public void output(String node, JmxQuery.JmxAttribute metric) throws IOException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
        try {
            final Map<String,Number> metrics = flatten(
                new HashMap<String,Number>(),
                metric.getAttributeName(),
                metric.getAttributeValue()
            );

            for (Map.Entry<String,Number> entry : metrics.entrySet()) {
                output.output(
                    node,
                    formatter.attributeName(metric.getBeanName(), entry.getKey()),
                    entry.getValue()
                );
            }
        } catch(Exception e) {
            System.err.println(String.format("Failed to read attribute %s [%s]", metric.getAttributeName(), e.getMessage()));
        }
    }

    private Map<String,Number> flatten(Map<String,Number> acc, String attribute, Object value) throws IOException {
        if (value instanceof Number) {

            acc.put(attribute, (Number) value);

        } else if (value instanceof String) {

            final Number v = enums.resolve(attribute, (String) value);
            if (v != null) {
                acc.put(attribute, v);
            } else {
                System.err.println(String.format("Missing enum for attribute %s [%s]", attribute, value));
            }

        } else if (value instanceof Set) {

            final Set set = (Set) value;
            acc.put(attribute + "__size", set.size());
            for(Object entry : set) {
                acc.put(attribute + "___" + entry, 1);
            }

        } else if (value instanceof List) {

            final List list = (List)value;
            acc.put(attribute + "__size", list.size());
            for(int i = 0; i<list.size(); i++) {
                flatten(acc, attribute + "___" + i, list.get(i));
            }

        } else if (value instanceof Map) {

            final Map<?,?> map = (Map<?,?>) value;
            acc.put(attribute + "__size", map.size());
            for(Map.Entry<?, ?> entry : map.entrySet()) {
                flatten(acc, attribute + "___" + entry.getKey(), entry.getValue());
            }

        } else if (value instanceof CompositeData) {

            CompositeData composite = (CompositeData) value;
            CompositeType type = composite.getCompositeType();
            TreeSet<String> keysSet = new TreeSet<String>(type.keySet());
            acc.put(attribute + "__size", keysSet.size());
            for(String key : keysSet) {
                flatten(acc, attribute + "___" + key, composite.get(key));
            }

        } else {
            System.err.println(String.format("Failed to convert [%s]", attribute));
        }

        return acc;
    }

    public void close() throws IOException {
        output.close();
    }
}
