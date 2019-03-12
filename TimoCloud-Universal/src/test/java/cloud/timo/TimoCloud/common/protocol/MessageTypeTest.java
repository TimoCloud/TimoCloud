package cloud.timo.TimoCloud.common.protocol;

import cloud.timo.TimoCloud.communication.CommunicationTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;

public class MessageTypeTest extends CommunicationTest {

    @Test
    public void testIdUniqueness() {
        Set<Integer> used = new HashSet<>();
        for (MessageType messageType : MessageType.values()) {
            assertFalse(used.contains(messageType.getId()));
            used.add(messageType.getId());
        }
    }

}