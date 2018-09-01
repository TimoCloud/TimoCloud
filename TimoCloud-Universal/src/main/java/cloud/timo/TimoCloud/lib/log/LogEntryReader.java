package cloud.timo.TimoCloud.lib.log;

import cloud.timo.TimoCloud.api.objects.log.LogLevel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntryReader implements Consumer<String> {

    private static final LogLevel FALLBACK_LOG_LEVEL = LogLevel.INFO;
    private static final Pattern LOG_LEVEL_PATTERN = Pattern.compile("\\[(.*?)\\]");

    private static Map<String, LogLevel> determinedLogLevels = new HashMap<>();

    private Consumer<LogEntry> logEntryConsumer;
    private LogLevel defaultLogLevel;

    public LogEntryReader(Consumer<LogEntry> logEntryConsumer) {
        this.logEntryConsumer = logEntryConsumer;
    }

    public LogEntryReader(Consumer<LogEntry> logEntryConsumer, LogLevel defaultLogLevel) {
        this(logEntryConsumer);
        this.defaultLogLevel = defaultLogLevel;
    }

    @Override
    public void accept(String message) {
        LogLevel logLevel = getLogLevel(message);
        String messageWithoutTimestamp = stripTimestamp(message);
        String strippedMessage = stripLogLevel(messageWithoutTimestamp);
        long timestamp = new Date().getTime();
        LogEntry logEntry = new LogEntry(timestamp, logLevel, strippedMessage, message);
        logEntryConsumer.accept(logEntry);
    }

    private LogLevel getLogLevel(String message) {
        LogLevel determined = determineLogLevel(extractLogLevelString(message));
        if (determined != null) return determined;
        if (defaultLogLevel != null) return defaultLogLevel;
        return FALLBACK_LOG_LEVEL;
    }

    private static String stripTimestamp(String message) {
        return message.trim().replaceFirst("\\[*\\]", "").trim();
    }

    private static String stripLogLevel(String message) {
        return message.trim().replaceFirst("\\[*\\]", "").trim();
    }

    private static String extractLogLevelString(String message) {
        Matcher matcher = LOG_LEVEL_PATTERN.matcher(message);
        if (! matcher.find()) return null;
        return matcher.group();
    }

    private static LogLevel determineLogLevel(String levelString) { // TODO Improve reading of log levels
        if (levelString == null) return null;
        if (determinedLogLevels.containsKey(levelString)) {
            return determinedLogLevels.get(levelString);
        }
        levelString = levelString.toLowerCase();
        LogLevel level = null;
        if (levelString.contains("info")) {
            level = LogLevel.INFO;
        }
        else if (levelString.contains("warn")) {
            level = LogLevel.WARNING;
        }
        else if (levelString.contains("severe") || levelString.contains("error") || levelString.contains("schwerwiegend")) {
            level = LogLevel.SEVERE;
        }
        if (level != null) {
            determinedLogLevels.put(levelString, level);
        }
        return level;
    }

}
