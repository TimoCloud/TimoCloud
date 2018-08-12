package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.TimoCloudTest;
import cloud.timo.TimoCloud.api.async.APIRequestType;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.objects.ServerGroup;
import cloud.timo.TimoCloud.lib.datatypes.TypeMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class APIRequestManagerTest extends TimoCloudTest {

    private APIRequestManager apiRequestManager;

    @Mock
    private CoreInstanceManager coreInstanceManager;
    @Mock
    private ServerGroup serverGroup;
    @Mock
    private ProxyGroup proxyGroup;
    @Mock
    private Server server;
    @Mock
    private Proxy proxy;

    @Before
    public void setUp() throws Exception {
        apiRequestManager = new APIRequestManager();
        when(getCore().getInstanceManager()).thenReturn(coreInstanceManager);

    }

    @Test
    public void processRequestGCreateServerGroup() {
        String name = "TestGroup";
        Integer onlineAmount = 4;
        Integer maxAmount = 15;
        Integer ram = 1098;
        Boolean isStatic = false;
        Integer priority = 3;
        String baseName = null;
        Collection<String> sortOutStates = Arrays.asList("INGAME", "RESTART");

        apiRequestManager.processRequest(new APIRequestImplementation<>(
                APIRequestType.G_CREATE_SERVER_GROUP, null, new TypeMap()
                .put("name", name)
        ));
    }
}