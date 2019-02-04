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
            assertNotNull(EventUtil.getClassByEventType(eventType));
        }
    }
}