package cloud.timo.TimoCloud.lib.log;

import cloud.timo.TimoCloud.api.implementations.objects.log.LogEntryObjectImplementation;
import cloud.timo.TimoCloud.api.objects.log.LogEntryObject;
import cloud.timo.TimoCloud.api.objects.log.LogLevel;

public class LogEntry implements Comparable<LogEntry> {

    private long timestamp;
    private LogLevel level;
    private String message;
    private String messageWithPrefix;

    public LogEntry(long timestamp, LogLevel level, String message, String messageWithPrefix) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.messageWithPrefix = messageWithPrefix;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    /**
     * @return The message as it is printed to the console, that means i.e. <b>[2018-08-15 20:06:45] [INFORMATION] Base BASE-1 connected.</b> instead of <b>Base BASE-1 connected.</b>
     */
    public String getMessageWithPrefix() {
        return messageWithPrefix;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry logEntry = (LogEntry) o;

        if (timestamp != logEntry.timestamp) return false;
        if (level != logEntry.level) return false;
        if (message != null ? !message.equals(logEntry.message) : logEntry.message != null) return false;
        return messageWithPrefix != null ? messageWithPrefix.equals(logEntry.messageWithPrefix) : logEntry.messageWithPrefix == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (messageWithPrefix != null ? messageWithPrefix.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(LogEntry o) {
        if (getTimestamp() == o.getTimestamp()) return 0;
        return getTimestamp() < o.getTimestamp() ? -1 : 1;
    }
}
