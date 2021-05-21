package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.managers.BaseInstanceManager;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.utils.HashUtil;
import cloud.timo.TimoCloud.common.utils.RandomIdGenerator;
import cloud.timo.TimoCloud.communication.CommunicationTest;
import cloud.timo.TimoCloud.core.managers.CoreFileManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class) // We need that in order to mock static methods
@PrepareForTest({
      HashUtil.class
})
public class ServerTest extends CommunicationTest {

    @Mock
    private BaseInstanceManager baseInstanceManager;
    @Mock
    private CoreFileManager coreFileManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(getBase().getInstanceManager()).thenReturn(baseInstanceManager);
        when(getCore().getFileManager()).thenReturn(coreFileManager);
    }

    @Test
    public void start() throws Exception {
        mockStatic(HashUtil.class);
        when(HashUtil.getHashes(any(File.class))).thenReturn(null);
        when(coreFileManager.getServerGlobalDirectory()).thenReturn(new File("core/templates/server/Global/"));
        doAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            getBaseStringHandler().handleMessage(message, message.toJson(), getBaseChannel());
            return null;
        }).when(getBaseObject()).sendMessage(any(Message.class));
        Server server = anyServer();
        server.start();
        ArgumentCaptor<BaseServerObject> argument = ArgumentCaptor.forClass(BaseServerObject.class);
        verify(TimoCloudBase.getInstance().getInstanceManager(), times(1)).addToServerQueue(argument.capture());
        BaseServerObject value = argument.getValue();
        assertEquals(server.getName(), value.getName());
        assertEquals(server.getId(), value.getId());
        assertEquals(server.getGroup().getName(), value.getGroup());
        assertEquals(server.isStatic(), value.isStatic());
        assertEquals(server.getMap(), value.getMap());
        assertEquals(server.getGroup().getRam(), value.getRam());
    }

    @Test
    public void stop() {

    }

    @Test
    public void register() {

    }

    @Test
    public void unregister() {

    }

    @Test
    public void onPlayerConnect() {

    }

    @Test
    public void onPlayerDisconnect() {

    }

    @Test
    public void getName() {

    }

    @Test
    public void getId() {

    }

    @Test
    public void getGroup() {

    }

    @Test
    public void getChannel() {

    }

    private Server anyServer() {
        return new Server("Test-1", "Test-1_1234567890", getBaseObject(), "RandomMap", anyGroup());
    }

    private ServerGroup anyGroup() {
        return new ServerGroup(RandomIdGenerator.generateId(), "Test", 3, 5, 1024, true, 2, null, Arrays.asList("OFFLINE", "STARTING", "INGAME"), Arrays.asList("--Dfile.encoding=UTF8", "--AABC"), Arrays.asList("-nogui", "-true"), "java");
    }

}