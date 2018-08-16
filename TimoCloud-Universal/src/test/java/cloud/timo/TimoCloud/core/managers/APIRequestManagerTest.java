package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.TimoCloudTest;
import cloud.timo.TimoCloud.api.async.APIRequestType;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.implementations.internal.TimoCloudInternalImplementationAPIBasicImplementation;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.objects.ServerGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        TimoCloudInternalAPI.class
})
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
        mockStatic(TimoCloudInternalAPI.class);
        when(TimoCloudInternalAPI.getImplementationAPI()).thenReturn(new TimoCloudInternalImplementationAPIBasicImplementation());
    }

    @Test
    public void processRequestGCreateServerGroup() {
        when(coreInstanceManager.getGroupByName(anyString())).thenReturn(null);
        ArgumentCaptor<ServerGroup> argumentCaptor = ArgumentCaptor.forClass(ServerGroup.class);
        ServerGroupProperties serverGroupProperties = new ServerGroupProperties("TestServerGroup")
                .setBaseName("BASE-2")
                .setMaxAmount(50)
                .setOnlineAmount(7)
                .setPriority(3)
                .setRam(1234)
                .setSortOutStates(Arrays.asList("TEST1", "TEST2"))
                .setStatic(true);
        apiRequestManager.processRequest(new APIRequestImplementation<>(
                APIRequestType.G_CREATE_SERVER_GROUP,
                null,
                serverGroupProperties)
        );
        verify(getCore().getInstanceManager(), times(1)).addGroup(argumentCaptor.capture());
        ServerGroup serverGroup = argumentCaptor.getValue();
        assertServerGroupPropertiesEquals(serverGroupProperties, serverGroup);
    }

    @Test
    public void processRequestGCreateServerGroupInvalid1() {
        ServerGroupProperties serverGroupProperties = new ServerGroupProperties("TestServerGroup")
                .setRam(-1);
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                APIRequestType.G_CREATE_SERVER_GROUP,
                null,
                serverGroupProperties)
        ).isSuccess());
    }

    @Test
    public void processRequestGCreateProxyGroup() {
        when(coreInstanceManager.getGroupByName(anyString())).thenReturn(null);
        ArgumentCaptor<ProxyGroup> argumentCaptor = ArgumentCaptor.forClass(ProxyGroup.class);
        ProxyGroupProperties proxyGroupProperties = new ProxyGroupProperties("TestProxyGroup")
                .setBaseName("BASE-3")
                .setHostNames(Arrays.asList("test.timo.cloud", "test1.timo.cloud"))
                .setKeepFreeSlots(33)
                .setMaxAmount(1)
                .setMaxPlayerCountPerProxy(123)
                .setMaxPlayerCount(321)
                .setMinAmount(1)
                .setMotd("&aTestMotd &bTimoCloud")
                .setProxyChooseStrategy(ProxyChooseStrategy.FILL)
                .setPriority(99)
                .setRam(5432)
                .setServerGroups(Arrays.asList("TestServerGroup"));
        apiRequestManager.processRequest(new APIRequestImplementation<>(
                APIRequestType.G_CREATE_PROXY_GROUP,
                null,
                proxyGroupProperties)
        );
        verify(getCore().getInstanceManager(), times(1)).addGroup(argumentCaptor.capture());
        ProxyGroup proxyGroup = argumentCaptor.getValue();
        assertProxyGroupPropertiesEquals(proxyGroupProperties, proxyGroup);
    }

    @Test
    public void processRequestGCreateProxyGroupInvalid() {
        ProxyGroupProperties proxyGroupProperties = new ProxyGroupProperties("TestProxyGroup")
                .setRam(-1);
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                APIRequestType.G_CREATE_PROXY_GROUP,
                null,
                proxyGroupProperties)
        ).isSuccess());
    }

    private void assertServerGroupPropertiesEquals(ServerGroupProperties serverGroupProperties, ServerGroup serverGroup) {
        assertNotNull(serverGroupProperties);
        assertNotNull(serverGroup);
        assertEquals(serverGroupProperties.getBaseName(), serverGroup.getBaseName());
        assertEquals((int) serverGroupProperties.getMaxAmount(), serverGroup.getMaxAmount());
        assertEquals(serverGroupProperties.getName(), serverGroup.getName());
        assertEquals((int) serverGroupProperties.getOnlineAmount(), serverGroup.getOnlineAmount());
        assertEquals((int) serverGroupProperties.getPriority(), serverGroup.getPriority());
        assertEquals((int) serverGroupProperties.getRam(), serverGroup.getRam());
        assertCollectionEqualsInAnyOrder(serverGroupProperties.getSortOutStates(), serverGroup.getSortOutStates());
        assertEquals(serverGroupProperties.isStatic(), serverGroup.isStatic());
    }

    private void assertProxyGroupPropertiesEquals(ProxyGroupProperties proxyGroupProperties, ProxyGroup proxyGroup) {
        assertNotNull(proxyGroupProperties);
        assertNotNull(proxyGroup);
        assertEquals(proxyGroupProperties.getBaseName(), proxyGroup.getBaseName());
        assertCollectionEqualsInAnyOrder(proxyGroupProperties.getHostNames(), proxyGroup.getHostNames());
        assertEquals((int) proxyGroupProperties.getKeepFreeSlots(), proxyGroup.getKeepFreeSlots());
        assertEquals((int) proxyGroupProperties.getMinAmount(), proxyGroup.getMinAmount());
        assertEquals((int) proxyGroupProperties.getMaxAmount(), proxyGroup.getMaxAmount());
        assertEquals((int) proxyGroupProperties.getMaxPlayerCount(), proxyGroup.getMaxPlayerCount());
        assertEquals((int) proxyGroupProperties.getMaxPlayerCountPerProxy(), proxyGroup.getMaxPlayerCountPerProxy());
        assertEquals(proxyGroupProperties.getMotd(), proxyGroup.getMotd());
        assertEquals(proxyGroupProperties.getName(), proxyGroup.getName());
        assertEquals((int) proxyGroupProperties.getPriority(), proxyGroup.getPriority());
        assertEquals(proxyGroupProperties.getProxyChooseStrategy(), proxyGroup.getProxyChooseStrategy());
        assertEquals((int) proxyGroupProperties.getRam(), proxyGroup.getRam());
        assertCollectionEqualsInAnyOrder(proxyGroupProperties.getServerGroups(), proxyGroup.getServerGroupNames());
        assertEquals(proxyGroupProperties.isStatic(), proxyGroup.isStatic());
    }
}