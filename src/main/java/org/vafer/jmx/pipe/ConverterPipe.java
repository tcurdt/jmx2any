package org.vafer.jmx.pipe;

import org.vafer.jmx.JmxQuery;
import org.vafer.jmx.formatter.DefaultFormatter;
import org.vafer.jmx.output.Enums;
import org.vafer.jmx.output.Output;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    private final Enums enums;

    public ConverterPipe(Output output, Enums enums) {
        this.output = output;
        this.enums = enums;
    }

    public void open() throws IOException {
        output.open();
    }

    public void output(String node, JmxQuery.JmxAttribute metric) throws IOException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
        final String attribute = formatter.attributeName(metric.getBeanName(), metric.getAttributeName());
        try {
            final Object value = metric.getAttributeValue();
            flatten(node, attribute, value);
        } catch(Exception e) {
            System.err.println(String.format("Failed to read attribute %s [%s]", attribute, e.getMessage()));
        }
    }

    private void flatten(String node, String attribute, Object value) throws IOException {
        if (value instanceof Number) {

            output.output(node, attribute, (Number) value);

        } else if (value instanceof String) {

            final Number v = enums.resolve(attribute, (String) value);
            if (v != null) {
                output.output(node, attribute, v);
            } else {
                System.err.println(String.format("Missing enum for attribute %s [%s]", attribute, value));
            }

        } else if (value instanceof Set) {

            final Set set = (Set) value;
            flatten(node, attribute + "__size", set.size());
            for(Object entry : set) {
                flatten(node, attribute + "___" + entry, 1);
            }

        } else if (value instanceof List) {

            final List list = (List)value;
            flatten(node, attribute + "__size", list.size());
            for(int i = 0; i<list.size(); i++) {
                flatten(node, attribute + "___" + i, list.get(i));
            }

        } else if (value instanceof Map) {

            final Map<?,?> map = (Map<?,?>) value;
            flatten(node, attribute + "__size", map.size());
            for(Map.Entry<?, ?> entry : map.entrySet()) {
                flatten(node, attribute + "___" + entry.getKey(), entry.getValue());
            }

        } else if (value instanceof CompositeData) {

            CompositeData composite = (CompositeData) value;
            CompositeType type = composite.getCompositeType();
            TreeSet<String> keysSet = new TreeSet<String>(type.keySet());
            flatten(node, attribute + "__size", keysSet.size());
            for(String key : keysSet) {
                flatten(node, attribute + "___" + key, composite.get(key));
            }

        } else {
            System.err.println(String.format("Failed to convert [%s]", attribute));
        }
    }

    public void close() throws IOException {
        output.close();
    }
}
