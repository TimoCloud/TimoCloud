package cloud.timo.TimoCloud.common.log;

import cloud.timo.TimoCloud.api.implementations.objects.log.LogEntryObjectImplementation;
import cloud.timo.TimoCloud.api.objects.log.LogEntryObject;
import cloud.timo.TimoCloud.api.objects.log.LogLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class LogEntry implements Comparable<LogEntry> {

    @Getter
    private final long nanoTime;
    @Getter
    private final long timestamp;
    @Getter
    private final LogLevel level;
    @Getter
    private final String message;
    @Getter
    private final String prefix;

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
        return Objects.equals(message, logEntry.message);
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
