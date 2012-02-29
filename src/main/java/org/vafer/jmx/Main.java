package org.vafer.jmx;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public final class Main {

    @Parameter(names = "-config", description = "path to config file", required = true)
    private String configPath = "/etc/jmx2any.yml";

    @Parameter(names = "-print", description = "print to output")
    private boolean print = false;

    @Parameter(names = "-all", description = "do not filter")
    private boolean all = false;

    private void run() throws Exception {
        Exporter exporter = new Exporter();
        Exporter.Config config = exporter.load(configPath, print, all);
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
