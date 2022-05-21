package cloud.timo.TimoCloud.common.log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

public class LoggingPrintStream extends PrintStream {

    private final Consumer<String> logger;
    private ByteArrayOutputStream byteArrayOutputStream;

    public LoggingPrintStream(Consumer<String> logger) {
        super(new ByteArrayOutputStream(0));
        this.logger = logger;
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) {
        byteArrayOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        byteArrayOutputStream.write(b, off, len);
    }

    private void newLine() {
        String line = byteArrayOutputStream.toString();
        if (line.endsWith("\n")) {
            line = line.substring(0, line.length() - 1);
        }
        logger.accept(line);
        try {
            byteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace(); // This should never happen
        } finally {
            this.byteArrayOutputStream = new ByteArrayOutputStream();
        }
    }

    @Override
    public void println() {
        super.println();
        newLine();
    }

    @Override
    public void println(boolean x) {
        super.println(x);
        newLine();
    }

    @Override
    public void println(char x) {
        super.println(x);
        newLine();
    }

    @Override
    public void println(int x) {
        super.println(x);
        newLine();
    }

    @Override
    public void println(long x) {
        super.println(x);
        newLine();
    }

    @Override
    public void println(float x) {
        super.println(x);
        newLine();
    }

    @Override
    public void println(double x) {
        super.println(x);
        newLine();
    }

    @Override
    public void println(char @NotNull [] x) {
        super.println(x);
        newLine();
    }

    @Override
    public void println(String x) {
        super.println(x);
        newLine();
    }

    @Override
    public void println(Object x) {
        super.println(x);
        newLine();
    }
}
