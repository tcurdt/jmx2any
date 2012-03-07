package org.vafer.jmx;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/*
* java -javaagent:/path/jmx2any.jar=/etc/jmx2any.yml yourmainclass
*/
public final class Agent {

    private final String filename;
    private final ScheduledThreadPoolExecutor executor;
    private final boolean console;
    private final boolean all;

    public Agent(String filename, boolean console, boolean all) {
        this.filename = filename;
        this.console = console;
        this.all = all;
        this.executor = new ScheduledThreadPoolExecutor(1);
        this.executor.setThreadFactory(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("jmx2any");
                return t;
            }
        });
    }

    public void start() {
        try {
            final Exporter exporter = new Exporter();
            final Exporter.Config config = exporter.load(filename, console, all);
            executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    try {
                        exporter.output(config);
                    } catch (Exception e) {
                        System.err.println("jmx2any: " + e.getMessage());
                    }
                    if (console) {
                        System.out.println("-");
                    }
                }
            }, config.initialDelay, config.repeatDelay, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.err.println("jmx2any: " + e.getMessage());
        }
    }

    public void stop() {
        executor.shutdown();
    }

    public static void premain(String args, Instrumentation inst) {
        System.out.println("Starting jmx2any agent");
        new Agent(args, false, false).start();
    }

//    public static void main(String[] args) throws Exception {
//        premain("/Users/tcurdt/Projects/jmx2any/src/examples/config.yaml", null);
//        while(true) {
//            Thread.sleep(10*1000);
//        }
//    }
}
