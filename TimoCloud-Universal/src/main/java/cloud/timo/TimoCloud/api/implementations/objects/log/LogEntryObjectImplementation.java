package cloud.timo.TimoCloud.api.implementations.objects.log;

import cloud.timo.TimoCloud.api.objects.log.LogEntryObject;
import cloud.timo.TimoCloud.api.objects.log.LogLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LogEntryObjectImplementation implements LogEntryObject {

    private long timestamp;
    private LogLevel level;
    private String message;
    private String messageWithPrefix;

    public LogEntryObjectImplementation(long timestamp, LogLevel level, String message, String messageWithPrefix) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.messageWithPrefix = messageWithPrefix;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public LogLevel getLevel() {
        return level;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageWithPrefix() {
        return messageWithPrefix;
    }
}
