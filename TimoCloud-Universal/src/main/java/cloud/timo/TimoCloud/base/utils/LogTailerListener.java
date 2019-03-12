package cloud.timo.TimoCloud.base.utils;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.utils.files.tailer.FileTailerListener;

import java.util.function.Consumer;

public class LogTailerListener extends FileTailerListener {

    private Consumer<String> logEntryReader;

    public LogTailerListener(Consumer<String> logEntryReader) {
        this.logEntryReader = logEntryReader;
    }

    @Override
    public void readLine(String line) {
        if (line.isEmpty() || line.trim().equals(">")) return;
        logEntryReader.accept(line);
    }

    @Override
    public void handleException(Exception e) {
        TimoCloudLogger.getLogger().severe(e);
    }

}
