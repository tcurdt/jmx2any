package org.vafer.jmx.output;

import java.util.Locale;
import java.util.Map;

import static org.vafer.jmx.output.GangliaOutput.UDPAddressingMode;
import static org.vafer.jmx.output.GangliaOutput.ProtocolVersion;

public final class OutputFactory {

    public static Output createOutput(Map<String, ?> config) {
        final String type = ((String) config.get("type")).toLowerCase(Locale.US);

        if ("ganglia".equals(type)) {

            String group = (String) config.get("address");
            Integer port = (Integer) config.get("port");
            UDPAddressingMode mode = UDPAddressingMode.valueOf((String)config.get("mode"));
            ProtocolVersion version = ProtocolVersion.valueOf((String)config.get("version"));
            return new GangliaOutput(group, port, mode, version);

        } else if ("graphite".equals(type)) {

            String host = (String) config.get("address");
            Integer port = (Integer) config.get("port");
            return new GraphiteOutput(host, port);

        } else if ("nagios".equals(type)) {

            return new NagiosOutput();

        } else if ("file".equals(type)) {

            String filename = (String) config.get("path");
            return new FileOutput(filename);

        } else {

            System.err.println("jmx2any: unknown output type [" + type + "]");
            return new ConsoleOutput();

        }
    }
}
