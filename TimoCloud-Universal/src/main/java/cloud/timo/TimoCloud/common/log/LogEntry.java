package cloud.timo.TimoCloud.common.log;

import cloud.timo.TimoCloud.api.implementations.objects.log.LogEntryObjectImplementation;
import cloud.timo.TimoCloud.api.objects.log.LogEntryObject;
import cloud.timo.TimoCloud.api.objects.log.LogLevel;

public class LogEntry implements Comparable<LogEntry> {

    private long nanoTime;
    private long timestamp;
    private LogLevel level;
    private String prefix;
    private String message;

    public LogEntry(long nanoTime, long timestamp, LogLevel level, String message, String prefix) {
        this.nanoTime = nanoTime;
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.prefix = prefix;
    }

    /**
     * @return The nano time can be used to retrieve the order of log entries
     */
    public long getNanoTime() {
        return nanoTime;
    }

    /**
     * @return Unix timestamp
     */
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
                getNanoTime(),
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

        if (nanoTime != logEntry.nanoTime) return false;
        if (level != logEntry.level) return false;
        return message != null ? message.equals(logEntry.message) : logEntry.message == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (nanoTime ^ (nanoTime >>> 32));
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(LogEntry o) {
        if (getNanoTime() == o.getNanoTime()) return 0;
        return getNanoTime() < o.getNanoTime() ? -1 : 1;
    }
}
