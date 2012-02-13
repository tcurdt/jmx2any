package org.vafer.jmx.output;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public final class Enums {

    private final TreeMap<String, LinkedHashMap<Integer, Pattern>> sections = new TreeMap<String, LinkedHashMap<Integer, Pattern>>();

    public Enums setMapping(String metric, Pattern pattern, Integer number) {
        LinkedHashMap<Integer, Pattern> section = sections.get(metric);
        if (section == null) {
            section = new LinkedHashMap<Integer, Pattern>();
            sections.put(metric, section);
        }
        section.put(number, pattern);
        return this;
    }

    public Number resolve(String metric, String value) {
        LinkedHashMap<Integer, Pattern> section = sections.get(metric);
        if (section == null) {
            return null;
        }
        for(Map.Entry<Integer, Pattern> entry : section.entrySet()) {
            if (entry.getValue().matcher(value).matches()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String toString() {
        return sections.toString();
    }
}
