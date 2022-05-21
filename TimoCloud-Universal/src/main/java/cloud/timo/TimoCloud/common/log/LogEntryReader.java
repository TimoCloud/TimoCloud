package cloud.timo.TimoCloud.common.log;

import cloud.timo.TimoCloud.api.objects.log.LogLevel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntryReader implements Consumer<String> {

    private static final LogLevel FALLBACK_LOG_LEVEL = LogLevel.INFO;
    private static final Pattern LOG_LEVEL_SEARCH_PATTERN = Pattern.compile("\\[(.*?)]");
    private static final Pattern PREFIX_SEARCH_PATTERN = Pattern.compile("(^.*?\\[.*?] ?(\\[.*?])?:? ?)");

    private static final Map<String, LogLevel> determinedLogLevels = new HashMap<>();

    private final Consumer<LogEntry> logEntryConsumer;
    private LogLevel defaultLogLevel;

    public LogEntryReader(Consumer<LogEntry> logEntryConsumer) {
        this.logEntryConsumer = logEntryConsumer;
    }

    public LogEntryReader(Consumer<LogEntry> logEntryConsumer, LogLevel defaultLogLevel) {
        this(logEntryConsumer);
        this.defaultLogLevel = defaultLogLevel;
    }

    private static String stripBrackets(String message) {
        return message.trim().replaceFirst(PREFIX_SEARCH_PATTERN.pattern(), "").trim();
    }

    private static String extractLogLevelString(String message) {
        Matcher matcher = LOG_LEVEL_SEARCH_PATTERN.matcher(message);
        if (!matcher.find()) return null;
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
        } else if (levelString.contains("warn")) {
            level = LogLevel.WARNING;
        } else if (levelString.contains("severe") || levelString.contains("error") || levelString.contains("schwerwiegend")) {
            level = LogLevel.SEVERE;
        }
        if (level != null) {
            determinedLogLevels.put(levelString, level);
        }
        return level;
    }

    @Override
    public void accept(String message) {
        LogLevel logLevel = getLogLevel(message);
        Matcher prefixMatcher = PREFIX_SEARCH_PATTERN.matcher(message);
        String prefix = prefixMatcher.find() ? prefixMatcher.group() : "";
        String strippedMessage = stripBrackets(message);
        long timestamp = new Date().getTime();
        long nanoTime = System.nanoTime();
        LogEntry logEntry = new LogEntry(nanoTime, timestamp, logLevel, strippedMessage, prefix);
        logEntryConsumer.accept(logEntry);
    }

    private LogLevel getLogLevel(String message) {
        LogLevel determined = determineLogLevel(extractLogLevelString(message));
        if (determined != null) return determined;
        if (defaultLogLevel != null) return defaultLogLevel;
        return FALLBACK_LOG_LEVEL;
    }

}
