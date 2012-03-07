package org.vafer.jmx;

import org.vafer.jmx.output.*;
import org.vafer.jmx.pipe.CompositePipe;
import org.vafer.jmx.pipe.ConverterPipe;
import org.vafer.jmx.pipe.JmxPipe;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Pattern;

public final class Exporter {

    public static class Config {

        public final String node;
        public final Map<String, Set<String>> queries;
        public final JmxPipe output;
        public final long initialDelay;
        public final long repeatDelay;

        public Config(String node, Map<String, Set<String>> queries, JmxPipe output, int initialDelay, int repeatDelay) {
            this.node = node;
            this.queries = queries;
            this.output = output;
            this.initialDelay = initialDelay;
            this.repeatDelay = repeatDelay;
        }
    }

    private int parseTimespan(String s) {
        int unit = 0;
        if (s.endsWith("ms")) {
            unit = 1;
        } else if (s.endsWith("s")) {
            unit = 1000;
        }
        int n = Integer.parseInt(s.replaceAll("[^0-9]", ""));
        return n * unit;
    }
    
    public Config load(String configfile, boolean console, boolean all) throws Exception {
        Collection<JmxPipe> pipes = new ArrayList<JmxPipe>();
        Map<String, Set<String>> queriesByUrl = new TreeMap<String, Set<String>>();

        Map<String,?> configMap = (Map) new Yaml().load(new FileInputStream(configfile));

        Map<String,?> nodeMap = (Map) configMap.get("node");
        String node = (String) nodeMap.get("id");
        int delay = parseTimespan(String.valueOf(nodeMap.get("delay")));

        if (delay <= 0) {
            throw new Exception("Please specify a delay in s or ms");
        }
        
        Map<String,?> outputsMap = (Map) configMap.get("output");
        for(String key : outputsMap.keySet()) {

            Map outputMap = new HashMap<String,Object>();
            outputMap.put("type", key);
            outputMap.put("report", "report");
            outputMap.putAll((Map) outputsMap.get(key));

            Output output;
            if (console) {
                output = new ConsoleOutput();
            } else {
                output = OutputFactory.createOutput(outputMap);
            }

            Set<String> reports = flattenAsStringSet(outputMap, "report");
            for(String report : reports) {
                Enums enums = new Enums();
                Set<String> metrics = new HashSet<String>();

                Map reportMap = (Map) configMap.get(report);
                if (reportMap == null) throw new Exception("No such report named \"" + report + "\"");

                String url = String.valueOf(reportMap.get("url"));
                Set<String> queries = queriesByUrl.get(url);
                if (queries == null) {
                    queries = new TreeSet<String>();
                    queriesByUrl.put(url, queries);
                }
                queries.addAll(flattenAsStringSet(reportMap, "query"));

                String prefix = (String) reportMap.get("prefix");
                if (prefix == null) {
                    prefix = "";
                }

                Map metricsMap = (Map) reportMap.get("metrics");
                metrics.addAll(metricsMap.keySet());
                for(Object metric : metricsMap.keySet()) {
                    Map metricMap = (Map) metricsMap.get(metric);
                    if (metricMap != null) {
                        Map enumsMap = (Map) metricMap.get("enum");
                        for(Object replacement : enumsMap.keySet()) {
                            enums.setMapping(
                                    prefix + String.valueOf(metric),
                                    Pattern.compile(String.valueOf(enumsMap.get(replacement))),
                                    Integer.parseInt(String.valueOf(replacement))
                            );
                        }
                    }
                }

                if (all) {
                    pipes.add(new ConverterPipe(output, prefix, enums));
                } else {
                    pipes.add(new OutputFilter(new ConverterPipe(output, prefix, enums), metrics));
                }
            }
        }

        return new Config(node, queriesByUrl, new CompositePipe(pipes), delay, delay);
    }

    public void output(Config config) throws Exception {
        String node = config.node;
        Map<String, Set<String>> queries = config.queries;
        JmxPipe output = config.output;

        output.open();
        for(String url : config.queries.keySet()) {
            JmxQuery query = new JmxQuery(url, queries.get(url));
            for(JmxQuery.JmxBean bean : query) {
                for(JmxQuery.JmxAttribute attribute : bean) {
                    output.output(node, attribute);
                }
            }
            query.close();
        }
        output.close();
    }

    private static Set<String> flattenAsStringSet(Map map, String key) {
        Set<String> result = new TreeSet<String>();
        Object report = map.get(key);
        if (report instanceof Collection) {
            for(Object r : (Collection) report) {
                result.add(String.valueOf(r));
            }
        } else {
            result.add(String.valueOf(report));
        }
        return result;
    }
}
