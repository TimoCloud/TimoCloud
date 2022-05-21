package cloud.timo.TimoCloud.common.global.logging;

import cloud.timo.TimoCloud.common.log.LoggingPrintStream;

/**
 * This class enables global methods to log to the currently active module
 */
public interface TimoCloudLogger {

    static TimoCloudLogger getLogger() {
        return TimoCloudLoggerHolder.logger;
    }

    static void setLogger(TimoCloudLogger logger) {
        TimoCloudLoggerHolder.logger = logger;
    }

    void info(String message);

    void warning(String message);

    void severe(String message);

    default void severe(Throwable throwable) {
        throwable.printStackTrace(new LoggingPrintStream(this::severe));
    }

    class TimoCloudLoggerHolder {
        private static TimoCloudLogger logger;
    }

}
