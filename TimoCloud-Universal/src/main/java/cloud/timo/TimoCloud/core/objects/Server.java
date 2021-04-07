package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.server.*;
import cloud.timo.TimoCloud.api.implementations.objects.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.common.events.EventTransmitter;
import cloud.timo.TimoCloud.common.json.JsonConverter;
import cloud.timo.TimoCloud.common.log.LogEntry;
import cloud.timo.TimoCloud.common.log.LogStorage;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.utils.DoAfterAmount;
import cloud.timo.TimoCloud.common.utils.HashUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ServerObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Server implements Instance, Communicatable {

    private String name;
    private String id;
    private int port;
    private ServerGroup group;
    private Channel channel;
    private Base base;
    private InetSocketAddress address;
    private String state = "STARTING";
    private String extra = "";
    private String motd = "";
    private final Set<PlayerObject> onlinePlayers;
    private int onlinePlayerCount = 0;
    private int maxPlayers = 0;
    private String map;
    private boolean starting;
    private boolean registered;
    private boolean connected;
    private LogStorage logStorage;
    private PublicKey publicKey;

    private DoAfterAmount templateUpdate;

    public Server(String name, String id, Base base, String map, ServerGroup group) {
        this.name = name;
        this.id = id;
        this.group = group;
        this.base = base;
        this.address = new InetSocketAddress(base.getPublicAddress(), 0);
        this.onlinePlayers = Collections.synchronizedSet(new HashSet<>());
        this.map = map;
        if (this.map == null) this.map = "";
        this.logStorage = new LogStorage();
    }

    public boolean isStatic() {
        return getGroup().isStatic();
    }

    @Override
    public void start() {
        try {
            this.starting = true;
            Message message = Message.create()
                    .setType(MessageType.BASE_START_SERVER)
                    .set("name", getName())
                    .set("id", getId())
                    .set("group", getGroup().getName())
                    .set("ram", getGroup().getRam())
                    .set("static", getGroup().isStatic())
                    .setIfNotNull("map", getMap())
                    .set("globalHash", HashUtil.getHashes(TimoCloudCore.getInstance().getFileManager().getServerGlobalDirectory()))
                    .set("spigotParameters", getGroup().getSpigotParameters())
                    .set("javaParameters", getGroup().getJavaParameters())
                    .set("jrePath", getGroup().getJrePath());
            if (!getGroup().isStatic()) {
                File templateDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), getGroup().getName());
                File mapDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), getGroup().getName() + "_" + getMap());
                try {
                    templateDirectory.mkdirs();
                    if (hasMap()) mapDirectory.mkdirs();
                    message.set("templateHash", HashUtil.getHashes(templateDirectory));
                    if (hasMap()) message.set("mapHash", HashUtil.getHashes(mapDirectory));
                } catch (IOException e) {
                    TimoCloudCore.getInstance().severe("Error while hashing files while starting server " + getName() + ": ");
                    e.printStackTrace();
                    return;
                }
            }
            getBase().sendMessage(message);
            getBase().setReady(false);
            getBase().setAvailableRam(getBase().getAvailableRam() - getGroup().getRam());
            TimoCloudCore.getInstance().info("Told base " + getBase().getName() + " to start server " + getName() + ".");
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while starting server " + getName() + ": ");
            TimoCloudCore.getInstance().severe(e);
            return;
        }
        getGroup().addServer(this);
        getBase().addServer(this);
    }

    @Override
    public void stop() {
        sendMessage(Message.create().setType(MessageType.SERVER_STOP));
    }

    @Override
    public void onConnect(Channel channel) {
        setChannel(channel);
        if (isConnected()) return;
        this.connected = true;
        TimoCloudCore.getInstance().info("Server " + getName() + " connected.");
    }

    @Override
    public void onDisconnect() {
        this.connected = false;
        setChannel(null);
        unregister();
        TimoCloudCore.getInstance().info("Server " + getName() + " disconnected.");
        onShutdown();
    }

    /**
     * Called when the server is connected and completely loaded
     */
    @Override
    public void register() {
        if (isRegistered()) return;
        getGroup().onServerConnect(this);
        setState("ONLINE");
        for (ProxyGroup proxyGroup : TimoCloudCore.getInstance().getInstanceManager().getProxyGroups()) {
            if (!proxyGroup.getServerGroups().contains(getGroup())) continue;
            proxyGroup.registerServer(this);
        }
        this.starting = false;
        this.registered = true;
        TimoCloudCore.getInstance().info("Server " + getName() + " registered.");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new ServerRegisterEventBasicImplementation(toServerObject()));
    }

    /**
     * Calling when the server is unloaded
     */
    @Override
    public void unregister() {
        if (!isRegistered()) return;
        setState("OFFLINE");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new ServerUnregisterEventBasicImplementation(toServerObject()));
        for (ProxyGroup proxyGroup : TimoCloudCore.getInstance().getInstanceManager().getProxyGroups()) {
            if (!proxyGroup.getServerGroups().contains(getGroup())) continue;
            proxyGroup.unregisterServer(this);
        }

        this.registered = false;
    }

    /**
     * Called when the server is completely shut down
     */
    private void onShutdown() {
        getGroup().removeServer(this);
        getBase().removeServer(this);

        getBase().sendMessage(Message.create().setType(MessageType.BASE_SERVER_STOPPED).setData(getId()));
    }

    public void onPlayerConnect(PlayerObject playerObject) {
        getOnlinePlayers().add(playerObject);
    }

    public void onPlayerDisconnect(PlayerObject playerObject) {
        getOnlinePlayers().remove(playerObject);
    }

    @Override
    public void onMessage(Message message, Communicatable sender) {
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            case SERVER_SET_STATE:
                setState((String) data);
                break;
            case SERVER_SET_EXTRA:
                setExtra((String) data);
                break;
            case SERVER_SET_MOTD:
                setMotd((String) data);
                break;
            case SERVER_SET_MAP:
                setMap((String) data);
                break;
            case SERVER_SET_PLAYERS:
                setOnlinePlayerCount(Integer.parseInt(((String) data).split("/")[0]));
                setMaxPlayers(Integer.parseInt(((String) data).split("/")[1]));
                break;
            case SERVER_EXECUTE_COMMAND:
                executeCommand((String) data);
                break;
            case SERVER_STOP:
                stop();
                break;
            case BASE_SERVER_STARTED:
                setPort(((Number) message.get("port")).intValue());
                try {
                    setPublicKey(RSAKeyUtil.publicKeyFromBase64((String) message.get("publicKey")));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe(String.format("Error while setting public key of server %s, please report this!", getName()));
                    TimoCloudCore.getInstance().severe(e);
                }
                break;
            case BASE_SERVER_NOT_STARTED:
                //unregister();
                break;
            case SERVER_REGISTER:
                register();
                break;
            case SERVER_TRANSFER_FINISHED:
                getTemplateUpdate().addOne();
                break;
            case SERVER_LOG_ENTRY:
                if (isRegistered() && sender instanceof Base) break;
                LogEntry logEntry = JsonConverter.convertMapIfNecessary(data, LogEntry.class);
                logStorage.addEntry(logEntry);
                break;
            default:
                sendMessage(message);
        }
    }

    @Override
    public void sendMessage(Message message) {
        if (getChannel() != null) getChannel().writeAndFlush(message.toJson());
    }

    @Override
    public void onHandshakeSuccess() {
        sendMessage(Message.create().setType(MessageType.SERVER_HANDSHAKE_SUCCESS));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public ServerGroup getGroup() {
        return group;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    public Base getBase() {
        return base;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        String oldValue = getState();
        this.state = state;
        if (this.isRegistered()) EventTransmitter.sendEvent(new ServerStateChangeEventBasicImplementation(toServerObject(), oldValue, state));
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        String oldValue = getExtra();
        this.extra = extra;
        if (this.isRegistered()) EventTransmitter.sendEvent(new ServerExtraChangeEventBasicImplementation(toServerObject(), oldValue, extra));
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        String oldValue = getMotd();
        this.motd = motd;
        if (this.isRegistered() && ! motd.equals(oldValue)) {
            EventTransmitter.sendEvent(new ServerMotdChangeEventBasicImplementation(toServerObject(), oldValue, motd));
        }
    }

    public Set<PlayerObject> getOnlinePlayers() {
        return onlinePlayers;
    }

    public int getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
        setAddress(new InetSocketAddress(getAddress().getAddress(), port));
    }

    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    public void setOnlinePlayerCount(int onlinePlayerCount) {
        int oldValue = getOnlinePlayerCount();
        this.onlinePlayerCount = onlinePlayerCount;
        if (onlinePlayerCount != oldValue) {
            EventTransmitter.sendEvent(new ServerOnlinePlayerCountChangeEventBasicImplementation(toServerObject(), oldValue, onlinePlayerCount));
        }
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        int oldValue = getMaxPlayers();
        this.maxPlayers = maxPlayers;
        if (maxPlayers != oldValue) {
            EventTransmitter.sendEvent(new ServerMaxPlayersChangeEventBasicImplementation(toServerObject(), oldValue, maxPlayers));
        }
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        String oldValue = getMap();
        this.map = map;
        EventTransmitter.sendEvent(new ServerMapChangeEventBasicImplementation(toServerObject(), oldValue, map));
    }

    public boolean hasMap() {
        return !(getMap() == null || getMap().isEmpty());
    }

    public boolean isStarting() {
        return starting;
    }

    public LogStorage getLogStorage() {
        return logStorage;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        TimoCloudCore.getInstance().getInstanceManager().serverDataUpdated(this);
    }

    public DoAfterAmount getTemplateUpdate() {
        return templateUpdate;
    }

    public void setTemplateUpdate(DoAfterAmount templateUpdate) {
        this.templateUpdate = templateUpdate;
    }

    public void executeCommand(String command) {
        sendMessage(Message.create().setType(MessageType.SERVER_EXECUTE_COMMAND).setData(command));
    }

    public ServerObject toServerObject() {
        return new ServerObjectCoreImplementation(
                getName(),
                getId(),
                getGroup().toLink(),
                getState(),
                getExtra(),
                getMap(),
                getMotd(),
                getOnlinePlayers().stream().map(player -> ((PlayerObjectBasicImplementation) player).toLink()).collect(Collectors.toSet()),
                getOnlinePlayerCount(),
                getMaxPlayers(),
                getBase().toLink(),
                getAddress()
        );
    }

    public ServerObjectLink toLink() {
        return new ServerObjectLink(getId(), getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        return id.equals(server.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
