package org.vafer.jmx;

public final class Main {

    public static void main(String[] args) throws Exception {
        Exporter exporter = new Exporter();
        Exporter.Config config = exporter.load("/Users/tcurdt/Projects/jmx2any/config.yaml");
        exporter.output(config);
    }

}
