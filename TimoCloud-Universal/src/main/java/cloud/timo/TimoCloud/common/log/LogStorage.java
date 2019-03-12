package cloud.timo.TimoCloud.common.log;

import java.util.*;
import java.util.stream.Collectors;

public class LogStorage {

    private TreeMap<Long, Collection<LogEntry>> storage;

    public LogStorage() {
        storage = new TreeMap<>();
    }

    public void addEntry(LogEntry entry) {
        Long key = entry.getTimestamp();
        storage.putIfAbsent(key, new LinkedHashSet<>());
        storage.get(key).add(entry);
    }

    public void removeEntry(LogEntry entry) {
        Long key = entry.getTimestamp();
        if (! storage.containsKey(key)) return;
        storage.get(key).remove(entry);
    }

    public Collection<LogEntry> queryEntries(Long startTime, Long endTime) {
        if (startTime == null) startTime = -1L;
        if (endTime == null) endTime = new Date().getTime();
        return extractEntries(
                storage.subMap(startTime, true, endTime, true)
        );
    }

    public Collection<LogEntry> queryEntriesByStart(long startTime) {
        return extractEntries(
                storage.tailMap(startTime, true)
        );
    }

    public Collection<LogEntry> queryEntriesByEnd(long endTime) {
        return extractEntries(
                storage.headMap(endTime, true)
        );
    }

    private Collection<LogEntry> extractEntries(Map<Long, Collection<LogEntry>> map) {
        return map
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
