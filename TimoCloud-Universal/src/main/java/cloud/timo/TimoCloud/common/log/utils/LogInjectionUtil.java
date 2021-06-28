package cloud.timo.TimoCloud.common.log.utils;

import cloud.timo.TimoCloud.api.objects.log.LogLevel;
import cloud.timo.TimoCloud.common.log.LogEntry;
import cloud.timo.TimoCloud.common.log.LogEntryReader;
import cloud.timo.TimoCloud.common.log.PipingLoggingPrintStream;

import java.io.PrintStream;
import java.util.function.Consumer;

public class LogInjectionUtil {

    private static PrintStream formerSystemOut;
    private static PrintStream formerSystemErr;

    public static void saveSystemOutAndErr() {
        formerSystemOut = System.out;
        formerSystemErr = System.err;
    }

    public static void injectSystemOutAndErr(Consumer<LogEntry> sendLogEntry) {
        formerSystemOut = System.out;
        LogEntryReader outReader = new LogEntryReader(sendLogEntry, LogLevel.INFO);
        PipingLoggingPrintStream newOutStream = new PipingLoggingPrintStream(System.out, outReader);
        System.setOut(newOutStream);
        formerSystemErr = System.err;
        LogEntryReader errReader = new LogEntryReader(sendLogEntry, LogLevel.SEVERE);
        PipingLoggingPrintStream newErrStream = new PipingLoggingPrintStream(System.err, errReader);
        System.setErr(newErrStream);
    }

    public static void restoreSystemOutAndErr() {
        System.setOut(formerSystemOut);
        System.setErr(formerSystemErr);
    }

}
