package org.vafer.jmx.output;

import org.vafer.jmx.formatter.DefaultFormatter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.NumberFormat;

import javax.management.ObjectName;

public final class FileOutput implements Output {

    private final static Charset UTF8 = Charset.forName("UTF8");
    private final String filename;
    private FileOutputStream os;

    public FileOutput(String filename) {
        this.filename = filename;
    }

    public void open() throws IOException {
        os = new FileOutputStream(filename);
    }

    public void output(String node, String key, Number value) throws IOException {

//        final String v;
//
//        if (Double.isNaN(value.doubleValue())) {
//            v = "NaN";
//        } else {
//            final NumberFormat f = NumberFormat.getInstance();
//            f.setMaximumFractionDigits(2);
//            f.setGroupingUsed(false);
//            v = f.format(value);
//        }

        os.write(key.getBytes(UTF8));
        os.write(" ".getBytes(UTF8));
        os.write(String.valueOf(value).getBytes(UTF8));
        os.write("\n".getBytes(UTF8));
    }

    public void close() throws IOException {
        try {
            os.close();
        } finally {
            os = null;
        }
    }

}