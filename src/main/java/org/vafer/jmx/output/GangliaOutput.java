package org.vafer.jmx.output;

import ganglia.gmetric.GMetric;
import ganglia.gmetric.GMetricSlope;
import ganglia.gmetric.GMetricType;
import org.vafer.jmx.formatter.DefaultFormatter;

import javax.management.ObjectName;
import java.io.IOException;

public final class GangliaOutput implements Output {

    private final String group;
    private final int port;
    private final GMetric gm;

    public enum UDPAddressingMode {
        MULTICAST,
        UNICAST
    };

    public enum ProtocolVersion {
        VERSION30X,
        VERSION31X
    };

    public GangliaOutput(String group, int port, UDPAddressingMode mode, ProtocolVersion version) {
        this.group = group;
        this.port = port;
        this.gm = new GMetric(
                group, port,
                GMetric.UDPAddressingMode.valueOf(mode.toString()),
                version == ProtocolVersion.VERSION31X);
    }

    public void open() throws IOException {
    }

    public void output(String node, String key, Number value) throws IOException {
        try {

//        GMetric gm = new GMetric("239.2.11.72", 8649, GMetric.UDPAddressingMode.MULTICAST, true) ;
//
//        gm.announce(
//                "TORSTEN",
//                "1111",
//                GMetricType.INT32,
//                "UNITS",
//                GMetricSlope.BOTH,
//                600,
//                1,
//                "TESTGROUP");

            gm.announce(
                key,
                value.toString(),
                GMetricType.INT32,
                "UNITS",
                GMetricSlope.BOTH,
                60,
                1,
                group);
        } catch (Exception e) {
            throw new IOException("", e);
        }
    }

    public void close() throws IOException {
    }
}
