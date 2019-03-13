package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.communication.CommunicationTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class EventUtilTest extends CommunicationTest {

    /**
     * This test checks if a class is assigned to every EventType
     */
    @Test
    public void getClassByEventType() {
        for (EventType eventType : EventType.values()) {
            assertNotNull(String.format("No event class implementation found for event type '%s', please add an implementation of this event type to EventUtil.java.", eventType.name()), EventUtil.getClassByEventType(eventType));
        }
    }
}