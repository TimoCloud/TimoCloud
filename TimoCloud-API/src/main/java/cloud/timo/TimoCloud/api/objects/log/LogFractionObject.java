package cloud.timo.TimoCloud.api.objects.log;

import java.util.Collection;

/**
 * Represents a part of a log with a given start- &amp; end-timestamp
 */
public interface LogFractionObject {

    Collection<LogEntryObject> getLogEntries();

    /**
     * @return The timestamp at which this fraction starts
     */
    long getStartTime();

    /**
     * @return The timestamp at which this fraction ends
     */
    long getEndTime();

}
