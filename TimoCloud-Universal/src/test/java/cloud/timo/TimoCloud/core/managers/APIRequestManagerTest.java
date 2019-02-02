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

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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
        when(coreInstanceManager.getProxyGroupByName(anyString())).thenReturn(proxyGroup);
        when(coreInstanceManager.getServerGroupByName(anyString())).thenReturn(serverGroup);
        when(coreInstanceManager.getProxyByIdentifier(anyString())).thenReturn(proxy);
        when(coreInstanceManager.getServerByIdentifier(anyString())).thenReturn(server);
    }

    @Test
    public void processRequestGCreateServerGroupValid() {
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
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                APIRequestType.G_CREATE_SERVER_GROUP,
                null,
                serverGroupProperties)
        ).isSuccess());
        verify(getCore().getInstanceManager(), times(1)).addGroup(argumentCaptor.capture());
        ServerGroup serverGroup = argumentCaptor.getValue();
        assertServerGroupPropertiesEquals(serverGroupProperties, serverGroup);
    }

    @Test
    public void processRequestGCreateServerGroupInvalid() {
        ServerGroupProperties serverGroupProperties = new ServerGroupProperties("TestServerGroup")
                .setRam(-1);
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                APIRequestType.G_CREATE_SERVER_GROUP,
                null,
                serverGroupProperties)
        ).isSuccess());
        verify(getCore().getInstanceManager(), times(0)).addGroup(any(ServerGroup.class));
    }

    @Test
    public void processRequestGCreateProxyGroupValid() {
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
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                APIRequestType.G_CREATE_PROXY_GROUP,
                null,
                proxyGroupProperties)
        ).isSuccess());
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
        verify(getCore().getInstanceManager(), times(0)).addGroup(any(ProxyGroup.class));
    }

    @Test
    public void processRequestSGNotFound() {
        String groupName = "NotExisting";
        when(coreInstanceManager.getServerGroupByName(eq(groupName))).thenReturn(null);
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_DELETE,
                        groupName
                )
        ).isSuccess());
        verify(coreInstanceManager, times(0)).removeServerGroup(any(ServerGroup.class));
    }

    @Test
    public void processRequestSGSetMaxAmountValid() {
        Integer value = 3;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_MAX_AMOUNT,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verify(serverGroup, times(1)).setMaxAmount(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestSGSetMaxAmountInvalid() {
        Integer value = -1;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_MAX_AMOUNT,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(serverGroup);
    }

    @Test
    public void processRequestSGSetRamValid() {
        Integer value = 4096;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_RAM,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verify(serverGroup, times(1)).setRam(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestSGSetRamInvalid() {
        Integer value = -1;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_RAM,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(serverGroup);
    }

    @Test
    public void processRequestSGSetStaticValid() {
        Boolean value = true;
        ArgumentCaptor<Boolean> argumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_STATIC,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verify(serverGroup, times(1)).setStatic(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestSGSetStaticInvalid() {
        Boolean value = null;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_STATIC,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(serverGroup);
    }

    @Test
    public void processRequestSGSetPriorityValid() {
        Integer value = 7;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_PRIORITY,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verify(serverGroup, times(1)).setPriority(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestSGSetPriorityInvalid() {
        Integer value = null;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_PRIORITY,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(serverGroup);
    }

    @Test
    public void processRequestSGSetBaseValid() {
        String value = "BASE-19";
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_BASE,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verify(serverGroup, times(1)).setBaseName(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestSGSetBaseInvalid() {
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_BASE,
                        "TestServerGroup",
                        new HashMap()
                )
        ).isSuccess());
        verifyZeroInteractions(serverGroup);
    }

    @Test
    public void processRequestSGSetOnlineAmountValid() {
        Integer value = 14;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_ONLINE_AMOUNT,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verify(serverGroup, times(1)).setOnlineAmount(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestSGSetOnlineAmountInvalid() {
        Integer value = -1;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_ONLINE_AMOUNT,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(serverGroup);
    }

    @Test
    public void processRequestSGSetSortOutStatesValid() {
        Collection<String> value = Arrays.asList("OFFLINE", "TEST1", "TEST2", "RESTARTING");
        ArgumentCaptor<Collection<String>> argumentCaptor = ArgumentCaptor.forClass(Collection.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_SORT_OUT_STATES,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verify(serverGroup, times(1)).setSortOutStates(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestSGSetSortOutStatesInvalid() {
        Collection<String> value = null;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_SET_SORT_OUT_STATES,
                        "TestServerGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(serverGroup);
    }

    @Test
    public void processRequestSGDelete() {
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.SG_DELETE,
                        "TestServerGroup"
                )
        ).isSuccess());
        verify(coreInstanceManager, times(1)).removeServerGroup(serverGroup);
    }


    @Test
    public void processRequestPGNotFound() {
        String groupName = "NotExisting";
        when(coreInstanceManager.getProxyGroupByName(eq(groupName))).thenReturn(null);
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_DELETE,
                        groupName
                )
        ).isSuccess());
        verify(coreInstanceManager, times(0)).removeProxyGroup(any(ProxyGroup.class));
    }

    @Test
    public void processRequestPGSetMaxPlayerCountValid() {
        Integer value = 3;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MAX_PLAYER_COUNT,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setMaxPlayerCount(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetMaxPlayerCountInvalid() {
        Integer value = -1;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MAX_PLAYER_COUNT,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetMaxPlayerCountPerProxyValid() {
        Integer value = 3;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MAX_PLAYER_COUNT_PER_PROXY,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setMaxPlayerCountPerProxy(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetMaxPlayerCountPerProxyInvalid() {
        Integer value = -1;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MAX_PLAYER_COUNT_PER_PROXY,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetKeepFreeSlotsValid() {
        Integer value = 3;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_KEEP_FREE_SLOTS,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setKeepFreeSlots(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetKeepFreeSlotsInvalid() {
        Integer value = -1;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_KEEP_FREE_SLOTS,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetMinAmountValid() {
        Integer value = 3;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MIN_AMOUNT,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setMinAmount(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetMinAmountInvalid() {
        Integer value = -1;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MIN_AMOUNT,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetMaxAmountValid() {
        Integer value = 3;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MAX_AMOUNT,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setMaxAmount(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetMaxAmountInvalid() {
        Integer value = -1;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MAX_AMOUNT,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetRamValid() {
        Integer value = 2048;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_RAM,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setRam(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetRamInvalid() {
        Integer value = 0;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_RAM,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetMotdValid() {
        String value = "&aTestMotd &5for &bTimoCloud";
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MOTD,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setMotd(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetMotdInvalid() {
        String value = null;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_MOTD,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetStaticValid() {
        Boolean value = true;
        ArgumentCaptor<Boolean> argumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_STATIC,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setStatic(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetStaticInvalid() {
        Boolean value = null;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_STATIC,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetPriorityValid() {
        Integer value = 3;
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_PRIORITY,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setPriority(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetPriorityInvalid() {
        Integer value = null;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_PRIORITY,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetBaseValid() {
        String value = "BASE-5";
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_BASE,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setBaseName(argumentCaptor.capture());
        assertEquals(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetBaseInvalid() {
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_BASE,
                        "TestProxyGroup",
                        new HashMap()
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetProxyChooseStrategyValid() {
        String value = "RANDOM";
        ArgumentCaptor<ProxyChooseStrategy> argumentCaptor = ArgumentCaptor.forClass(ProxyChooseStrategy.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_PROXY_CHOOSE_STRATEGY,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setProxyChooseStrategy(argumentCaptor.capture());
        assertEquals(ProxyChooseStrategy.valueOf(value), argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetProxyChooseStrategyInvalid() {
        String value = "THIS_DOES_NOT_EXIST";
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_PROXY_CHOOSE_STRATEGY,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGSetHostNamesValid() {
        Set<String> value = new HashSet<>(Arrays.asList("test1.timo.cloud", "test2.timo.cloud"));
        ArgumentCaptor<Set<String>> argumentCaptor = ArgumentCaptor.forClass(Set.class);
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_HOST_NAMES,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verify(proxyGroup, times(1)).setHostNames(argumentCaptor.capture());
        assertCollectionEqualsInAnyOrder(value, argumentCaptor.getValue());
    }

    @Test
    public void processRequestPGSetHostNamesInvalid() {
        Set<String> value = null;
        assertFalse(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_SET_HOST_NAMES,
                        "TestProxyGroup",
                        value
                )
        ).isSuccess());
        verifyZeroInteractions(proxyGroup);
    }

    @Test
    public void processRequestPGDelete() {
        assertTrue(apiRequestManager.processRequest(new APIRequestImplementation<>(
                        APIRequestType.PG_DELETE,
                        "TestProxyGroup"
                )
        ).isSuccess());
        verify(coreInstanceManager, times(1)).removeProxyGroup(proxyGroup);
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