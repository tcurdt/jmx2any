package org.vafer.jmx.output;

import org.vafer.jmx.formatter.DefaultFormatter;

import java.text.NumberFormat;

import javax.management.ObjectName;

public final class FileOutput implements Output {

    public void open() {
    }

    public void output(String node, String key, Number value) {
        final String v;

        if (Double.isNaN(value.doubleValue())) {
            v = "U";
        } else {
            final NumberFormat f = NumberFormat.getInstance();
            f.setMaximumFractionDigits(2);
            f.setGroupingUsed(false);
            v = f.format(value);
        }

        System.out.println(key + ".value " + v);
    }

    public void close() {
    }

}