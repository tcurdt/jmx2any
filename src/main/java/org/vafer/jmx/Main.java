package org.vafer.jmx;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public final class Main {

//    @Parameter(names = "-url", description = "jmx url")
//    private String url = "service:jmx:rmi:///jndi/rmi://localhost:7199/jmxrmi";
//
//    @Parameter(names = "-query", description = "query")
//    private String query = "";

    @Parameter(names = "-config", description = "path to config file", required = true)
    private String configPath = "";

    private void run() throws Exception {
        Exporter exporter = new Exporter();
        Exporter.Config config = exporter.load(configPath);
        exporter.output(config);
    }

    public static void main(String[] args) throws Exception {
        Main m = new Main();
        JCommander cli = new JCommander(m);
        try {
            cli.parse(args);
        } catch(Exception e) {
            cli.usage();
            System.exit(1);
        }
        m.run();
    }
}
