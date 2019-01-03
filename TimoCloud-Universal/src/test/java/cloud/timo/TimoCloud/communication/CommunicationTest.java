package cloud.timo.TimoCloud.communication;

import cloud.timo.TimoCloud.TimoCloudTest;
import cloud.timo.TimoCloud.base.sockets.BaseSocketClientHandler;
import cloud.timo.TimoCloud.base.sockets.BaseSocketMessageManager;
import cloud.timo.TimoCloud.base.sockets.BaseStringHandler;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitSocketClientHandler;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitSocketMessageManager;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitStringHandler;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeSocketClientHandler;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeSocketMessageManager;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeStringHandler;
import cloud.timo.TimoCloud.cord.sockets.CordSocketClientHandler;
import cloud.timo.TimoCloud.cord.sockets.CordSocketMessageManager;
import cloud.timo.TimoCloud.cord.sockets.CordStringHandler;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.CoreSocketServerHandler;
import cloud.timo.TimoCloud.core.sockets.CoreStringHandler;
import cloud.timo.TimoCloud.lib.protocol.Message;
import cloud.timo.TimoCloud.lib.protocol.MessageType;
import io.netty.channel.Channel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class) // We need that for every test in order to mock static methods
@PrepareForTest({

})
@Ignore
public class CommunicationTest extends TimoCloudTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Server server;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Proxy proxy;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Base baseObject;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Cord cordObject;

    @Mock
    private Channel serverChannel;
    @Mock
    private Channel proxyChannel;
    @Mock
    private Channel baseChannel;
    @Mock
    private Channel cordChannel;

    @Mock
    private BukkitSocketMessageManager bukkitMessageManager;
    @Mock
    private BungeeSocketMessageManager bungeeMessageManager;
    @Mock
    private BaseSocketMessageManager baseMessageManager;
    @Mock
    private CordSocketMessageManager cordMessageManager;

    @Mock
    private CoreSocketServerHandler coreSocketHandler;
    @Mock
    private BaseSocketClientHandler baseSocketHandler;
    @Mock
    private BukkitSocketClientHandler bukkitSocketHandler;
    @Mock
    private BungeeSocketClientHandler bungeeSocketHandler;
    @Mock
    private CordSocketClientHandler cordSocketHandler;

    private CoreStringHandler coreStringHandler;
    private BaseStringHandler baseStringHandler;
    private BukkitStringHandler bukkitStringHandler;
    private BungeeStringHandler bungeeStringHandler;
    private CordStringHandler cordStringHandler;

    @Before
    public void setupCommunication() {
        when(getBukkit().getSocketMessageManager()).thenReturn(bukkitMessageManager);
        when(getBungee().getSocketMessageManager()).thenReturn(bungeeMessageManager);
        when(getBase().getSocketMessageManager()).thenReturn(baseMessageManager);
        when(getCord().getSocketMessageManager()).thenReturn(cordMessageManager);

        when(getCore().getSocketServerHandler()).thenReturn(coreSocketHandler);
        when(getBase().getSocketClientHandler()).thenReturn(baseSocketHandler);
        when(getBukkit().getSocketClientHandler()).thenReturn(bukkitSocketHandler);
        when(getBungee().getSocketClientHandler()).thenReturn(bungeeSocketHandler);
        when(getCord().getSocketClientHandler()).thenReturn(cordSocketHandler);

        when(server.getChannel()).thenReturn(serverChannel);
        when(proxy.getChannel()).thenReturn(proxyChannel);
        when(baseObject.getChannel()).thenReturn(baseChannel);
        when(cordObject.getChannel()).thenReturn(cordChannel);

        coreStringHandler = new CoreStringHandler();
        baseStringHandler = new BaseStringHandler();
        bukkitStringHandler = new BukkitStringHandler();
        bungeeStringHandler = new BungeeStringHandler();
        cordStringHandler = new CordStringHandler();

        doAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            bukkitStringHandler.handleMessage(message, message.toJson(), serverChannel);
            return null;
        }).when(server).sendMessage(any(Message.class));

        doAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            bungeeStringHandler.handleMessage(message, message.toJson(), proxyChannel);
            return null;
        }).when(proxy).sendMessage(any(Message.class));

        doAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            baseStringHandler.handleMessage(message, message.toJson(), baseChannel);
            return null;
        }).when(getBaseObject()).sendMessage(any(Message.class));

        doAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            cordStringHandler.handleMessage(message, message.toJson(), cordChannel);
            return null;
        }).when(cordObject).sendMessage(any(Message.class));

        doAnswer(invocation -> {
            Message message = invocation.getArgument(1);
            coreStringHandler.handleMessage(message, message.toJson(), serverChannel);
            return null;
        }).when(bukkitMessageManager).sendMessage(any(Message.class));

        doAnswer(invocation -> {
            Message message = invocation.getArgument(1);
            coreStringHandler.handleMessage(message, message.toJson(), proxyChannel);
            return null;
        }).when(bungeeMessageManager).sendMessage(any(Message.class));

        doAnswer(invocation -> {
            Message message = invocation.getArgument(1);
            coreStringHandler.handleMessage(message, message.toJson(), baseChannel);
            return null;
        }).when(baseMessageManager).sendMessage(any(Message.class));

        doAnswer(invocation -> {
            Message message = invocation.getArgument(1);
            coreStringHandler.handleMessage(message, message.toJson(), cordChannel);
            return null;
        }).when(cordMessageManager).sendMessage(any(Message.class));

        doAnswer(invocation -> {
            Channel channel = invocation.getArgument(0);
            Message message = invocation.getArgument(1);
            if (channel == serverChannel) {
                bukkitStringHandler.handleMessage(message, message.toJson(), channel);
            } else if (channel == proxyChannel) {
                bungeeStringHandler.handleMessage(message, message.toJson(), channel);
            } else if (channel == baseChannel) {
                baseStringHandler.handleMessage(message, message.toJson(), channel);
            } else if (channel == cordChannel) {
                cordStringHandler.handleMessage(message, message.toJson(), channel);
            }
            return null;
        }).when(coreSocketHandler).sendMessage(any(Channel.class), any(Message.class));

        doAnswer(invocation -> {
            String messageString = invocation.getArgument(0);
            coreStringHandler.handleMessage(Message.createFromJsonString(messageString), messageString, serverChannel);
            return null;
        }).when(bukkitSocketHandler).sendMessage(anyString());

        doAnswer(invocation -> {
            String messageString = invocation.getArgument(0);
            coreStringHandler.handleMessage(Message.createFromJsonString(messageString), messageString, proxyChannel);
            return null;
        }).when(bungeeSocketHandler).sendMessage(anyString());

        doAnswer(invocation -> {
            String messageString = invocation.getArgument(0);
            coreStringHandler.handleMessage(Message.createFromJsonString(messageString), messageString, baseChannel);
            return null;
        }).when(baseSocketHandler).sendMessage(anyString());

        doAnswer(invocation -> {
            String messageString = invocation.getArgument(0);
            coreStringHandler.handleMessage(Message.createFromJsonString(messageString), messageString, cordChannel);
            return null;
        }).when(cordSocketHandler).sendMessage(anyString());
    }

    private Message anyMessage() {
        return Message.create()
                .setType(MessageType.API_DATA)
                .setTarget(UUID.randomUUID().toString())
                .set("test", "TEST")
                .setData(Message.create()
                        .set("name", "Timo")
                        .set("age", 100)
                        .set("balance", 12345678.9));
    }

    public Server getServer() {
        return server;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Base getBaseObject() {
        return baseObject;
    }

    public Cord getCordObject() {
        return cordObject;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public Channel getProxyChannel() {
        return proxyChannel;
    }

    public Channel getBaseChannel() {
        return baseChannel;
    }

    public Channel getCordChannel() {
        return cordChannel;
    }

    public BukkitSocketMessageManager getBukkitMessageManager() {
        return bukkitMessageManager;
    }

    public BungeeSocketMessageManager getBungeeMessageManager() {
        return bungeeMessageManager;
    }

    public BaseSocketMessageManager getBaseMessageManager() {
        return baseMessageManager;
    }

    public CordSocketMessageManager getCordMessageManager() {
        return cordMessageManager;
    }

    public CoreSocketServerHandler getCoreSocketHandler() {
        return coreSocketHandler;
    }

    public BaseSocketClientHandler getBaseSocketHandler() {
        return baseSocketHandler;
    }

    public BukkitSocketClientHandler getBukkitSocketHandler() {
        return bukkitSocketHandler;
    }

    public BungeeSocketClientHandler getBungeeSocketHandler() {
        return bungeeSocketHandler;
    }

    public CordSocketClientHandler getCordSocketHandler() {
        return cordSocketHandler;
    }

    public CoreStringHandler getCoreStringHandler() {
        return coreStringHandler;
    }

    public BaseStringHandler getBaseStringHandler() {
        return baseStringHandler;
    }

    public BukkitStringHandler getBukkitStringHandler() {
        return bukkitStringHandler;
    }

    public BungeeStringHandler getBungeeStringHandler() {
        return bungeeStringHandler;
    }

    public CordStringHandler getCordStringHandler() {
        return cordStringHandler;
    }
}
