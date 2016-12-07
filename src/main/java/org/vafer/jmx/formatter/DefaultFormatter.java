package org.vafer.jmx.formatter;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Locale;

public final class DefaultFormatter {

    private static String fieldname(String s) {
        return s.replaceAll("[^A-Za-z0-9]", "_").replaceAll("_$", "");
    }

    private static String beanString(ObjectName beanName) {
        StringBuilder sb = new StringBuilder();
        sb.append(beanName.getDomain());

        Hashtable<String, String> properties = beanName.getKeyPropertyList();

        String keyspace = "keyspace";
        if (properties.containsKey(keyspace)) {
            sb.append('.');
            sb.append(properties.get(keyspace));
            properties.remove(keyspace);
        }

        String type = "type";
        if (properties.containsKey(type)) {
            sb.append('.');
            sb.append(properties.get(type));
            properties.remove(type);
        }

        ArrayList<String> keys = new ArrayList(properties.keySet());
        Collections.sort(keys);

        for(String key : keys) {
            sb.append('.');
            sb.append(properties.get(key));
        }

        return sb.toString();
    }

    public String attributeName(ObjectName bean, String attribute) {
        StringBuilder sb = new StringBuilder();
        sb.append(fieldname(beanString(bean)));
        sb.append('_');
        sb.append(fieldname(attribute));
        return sb.toString().toLowerCase(Locale.US);
    }
}
