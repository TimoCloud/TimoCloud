package cloud.timo.TimoCloud.common.log;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * Pipes write operations to a given OutputStream, combined with the functionality of a {@link LoggingPrintStream}
 */
public class PipingLoggingPrintStream extends LoggingPrintStream {

    private final OutputStream pipeStream;

    public PipingLoggingPrintStream(OutputStream pipeStream, Consumer<String> logger) {
        super(logger);
        this.pipeStream = pipeStream;
    }

    @Override
    public void write(int b) {
        try {
            pipeStream.write(b);
        } catch (Exception ignored) {
        }
        super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        try {
            pipeStream.write(b, off, len);
        } catch (IOException e) {
            TimoCloudLogger.getLogger().severe(e);
        }
        super.write(b, off, len);
    }

    @Override
    public void flush() {
        try {
            pipeStream.flush();
        } catch (Exception e) {
            TimoCloudLogger.getLogger().severe(e);
        }
        super.flush();
    }
}
