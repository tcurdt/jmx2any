package org.vafer.jmx.output;

import java.util.Map;

public final class OutputFactory {

    public static Output createOutput(Map<String, ?> config) {
        // System.out.println("" + config);
        return new ConsoleOutput();
    }
}
