package cloud.timo.TimoCloud.lib.log.utils;

import cloud.timo.TimoCloud.api.objects.log.LogLevel;
import cloud.timo.TimoCloud.lib.log.LogEntry;
import cloud.timo.TimoCloud.lib.log.LogEntryReader;
import cloud.timo.TimoCloud.lib.log.PipingLoggingPrintStream;

import java.util.function.Consumer;

public class LogInjectionUtil {

    public static void injectSystemOutAndErr(Consumer<LogEntry> sendLogEntry) {
        LogEntryReader outReader = new LogEntryReader(sendLogEntry, LogLevel.INFO);
        PipingLoggingPrintStream newOutStream = new PipingLoggingPrintStream(System.out, outReader);
        System.setOut(newOutStream);
        LogEntryReader errReader = new LogEntryReader(sendLogEntry, LogLevel.SEVERE);
        PipingLoggingPrintStream newErrStream = new PipingLoggingPrintStream(System.err, errReader);
        System.setErr(newErrStream);
    }

}
