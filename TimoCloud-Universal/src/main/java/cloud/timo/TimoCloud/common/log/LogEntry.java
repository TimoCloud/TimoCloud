package cloud.timo.TimoCloud.common.log;

import cloud.timo.TimoCloud.api.implementations.objects.log.LogEntryObjectImplementation;
import cloud.timo.TimoCloud.api.objects.log.LogEntryObject;
import cloud.timo.TimoCloud.api.objects.log.LogLevel;

public class LogEntry implements Comparable<LogEntry> {

    private long timestamp;
    private LogLevel level;
    private String prefix;
    private String message;

    public LogEntry(long timestamp, LogLevel level, String message, String prefix) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.prefix = prefix;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMessage() {
        return message;
    }

    /**
     * @return The message as it is printed to the console, that means i.e. <b>[2018-08-15 20:06:45] [INFORMATION] Base BASE-1 connected.</b> instead of <b>Base BASE-1 connected.</b>
     */
    public String getMessageWithPrefix() {
        return getPrefix() + getMessage();
    }

    public LogEntryObject toLogEntryObject() {
        return new LogEntryObjectImplementation(
                getTimestamp(),
                getLevel(),
                getMessage(),
                getMessageWithPrefix()
        );
    }


    @Override
    public int compareTo(LogEntry o) {
        if (getTimestamp() == o.getTimestamp()) return 0;
        return getTimestamp() < o.getTimestamp() ? -1 : 1;
    }
}
