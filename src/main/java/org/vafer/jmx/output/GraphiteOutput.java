package org.vafer.jmx.output;

import org.vafer.jmx.formatter.DefaultFormatter;

import javax.management.ObjectName;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public final class GraphiteOutput implements Output {

    private final String host;
    private final int port;

    private Socket socket;
    private Writer writer;
    private long time;

    public GraphiteOutput(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void open() throws IOException {
        if (socket != null) return;
        socket = new Socket(host, port);
        writer = new OutputStreamWriter(socket.getOutputStream());
        time = System.currentTimeMillis() / 1000;
    }

    public void output(String node, String key, Number value) throws IOException {
        StringBuilder sb = new StringBuilder()
                .append(node).append('.')
                .append(key).append(' ')
                .append(value).append(' ')
                .append(time).append('\n');
        writer.write(sb.toString());
    }

    public void close() throws IOException {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
                writer = null;
            }
        } finally {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
    }
}
