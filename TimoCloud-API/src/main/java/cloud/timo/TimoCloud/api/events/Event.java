package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.events.EventType;

public interface Event {

    /**
     * @return The event's type
     */
    EventType getType();

}
