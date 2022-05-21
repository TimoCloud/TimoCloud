package cloud.timo.TimoCloud.common.log.utils;

import cloud.timo.TimoCloud.api.objects.log.LogLevel;
import cloud.timo.TimoCloud.common.log.LogEntry;
import cloud.timo.TimoCloud.common.log.LogEntryReader;
import cloud.timo.TimoCloud.common.log.PipingLoggingPrintStream;
import lombok.experimental.UtilityClass;

import java.io.PrintStream;
import java.util.function.Consumer;

@UtilityClass
public class LogInjectionUtil {

    private PrintStream formerSystemOut;
    private PrintStream formerSystemErr;

    public void saveSystemOutAndErr() {
        formerSystemOut = System.out;
        formerSystemErr = System.err;
    }

    public void injectSystemOutAndErr(Consumer<LogEntry> sendLogEntry) {
        formerSystemOut = System.out;
        LogEntryReader outReader = new LogEntryReader(sendLogEntry, LogLevel.INFO);
        PipingLoggingPrintStream newOutStream = new PipingLoggingPrintStream(System.out, outReader);
        System.setOut(newOutStream);
        formerSystemErr = System.err;
        LogEntryReader errReader = new LogEntryReader(sendLogEntry, LogLevel.SEVERE);
        PipingLoggingPrintStream newErrStream = new PipingLoggingPrintStream(System.err, errReader);
        System.setErr(newErrStream);
    }

    public void restoreSystemOutAndErr() {
        System.setOut(formerSystemOut);
        System.setErr(formerSystemErr);
    }

}
