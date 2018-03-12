package cloud.timo.TimoCloud.lib.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class LoggingOutputStream extends OutputStream {

    private Consumer<String> logger;
    private StringBuilder stringBuilder;

    public LoggingOutputStream(Consumer<String> logger) {
        this.logger = logger;
        stringBuilder = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (b & 0xff);
        stringBuilder.append(new String(bytes));

        if (stringBuilder.length() >= 2 && stringBuilder.charAt(stringBuilder.length()-1) == '\n') {
            logger.accept(stringBuilder.substring(0, stringBuilder.length()-1));
            stringBuilder = new StringBuilder();
        }
    }

    @Override
    public void flush() throws IOException {
        logger.accept(stringBuilder.toString());
        stringBuilder = new StringBuilder();
    }
}
