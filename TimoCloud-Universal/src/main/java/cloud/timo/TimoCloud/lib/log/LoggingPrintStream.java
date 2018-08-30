package cloud.timo.TimoCloud.lib.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

public class LoggingPrintStream extends PrintStream {

    private Consumer<String> logger;
    private ByteArrayOutputStream byteArrayOutputStream;

    public LoggingPrintStream(Consumer<String> logger) {
        super(new ByteArrayOutputStream(0));
        this.logger = logger;
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) {
        byteArrayOutputStream.write(b);
        if (b == '\n') {
            flush();
        }
        super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        byteArrayOutputStream.write(b, off, len);
        if (b.length > 0 && b[b.length-1] == '\n') {
            flush();
        }
        super.write(b, off, len);
    }

    @Override
    public void flush() {
        logger.accept(byteArrayOutputStream.toString());
        try {
            byteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace(); // This should never happen
        }
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        super.flush();
    }
}
